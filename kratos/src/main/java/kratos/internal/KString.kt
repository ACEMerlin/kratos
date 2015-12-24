package kratos.internal

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlin.properties.Delegates

/**
 * Created by merlin on 15/11/19.
 */
class KString() {

    interface OnUpdateListener {
        fun update(view: View, new: String)
    }

    private var views = emptySet<View>()
    var updateFn: ((view: View, new: String) -> Unit)? = null
    var onUpdateListener: OnUpdateListener? = null
    var initData: String? = null

    private var data: String by Delegates.observable("") {
        d, old, new ->
        if ((old != new)) {
            for (view in views) {
                Log.d("KString", "VIEW UPDATE!: ${view.resources.getResourceName(view.id)} FROM $old TO $new")
                if (updateFn != null)
                    updateFn!!(view, new)
                else if (onUpdateListener != null)
                    onUpdateListener!!.update(view, new)
                else
                    view.updateText(new)
            }
        }
    }

    public fun bind(view: View) {
        Log.d("KString", "VIEW BINDED!: ${view.resources.getResourceName(view.id)} TO ${this}")
        this.views += view
        this.views.forEach {
            if (it is EditText) {
                it.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        data = s.toString()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }
                })
            }
        }
    }

    fun get(): String {
        return data
    }

    fun set(new: String) {
        data = new
    }
}

public fun TextView.updateText(new: String) {
    this.text = new
}

public fun ImageView.updateText(new: String) {
    //TODO 对照组
}

public fun EditText.updateText(new: String) {
    val position = this.selectionStart
    this.setText(new)
    this.setSelection(position)
}

public fun View.updateText(new: String): Unit = when (this) {
    is EditText -> this.updateText(new)
    is TextView -> this.updateText(new)
    is ImageView -> this.updateText(new)
    else -> throw NoSuchMethodException("no update method on $this")
}


