package me.rei_m.hbfavmaterial.fragments

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatTextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import com.jakewharton.rxbinding.widget.RxTextView
import com.squareup.otto.Subscribe
import me.rei_m.hbfavmaterial.App
import me.rei_m.hbfavmaterial.R
import me.rei_m.hbfavmaterial.events.EventBusHolder
import me.rei_m.hbfavmaterial.events.ui.UserIdCheckedEvent
import me.rei_m.hbfavmaterial.extensions.adjustScreenWidth
import me.rei_m.hbfavmaterial.extensions.getAppContext
import me.rei_m.hbfavmaterial.extensions.showSnackbarNetworkError
import me.rei_m.hbfavmaterial.extensions.toggle
import me.rei_m.hbfavmaterial.models.UserModel
import rx.Subscription
import javax.inject.Inject

class EditUserIdDialogFragment : DialogFragment(), IProgressDialog {

    @Inject
    lateinit var userModel: UserModel

    override var mProgressDialog: ProgressDialog? = null

    lateinit private var mSubscription: Subscription

    companion object {

        final val TAG = EditUserIdDialogFragment::class.java.simpleName

        fun newInstance(): EditUserIdDialogFragment {
            return EditUserIdDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as App).component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.dialog_fragment_edit_user_id, container, false)

        with(view.findViewById(R.id.dialog_fragment_edit_user_id_text_title)) {
            this as AppCompatTextView
            text = getString(R.string.dialog_title_set_user)
        }

        val editUserId = view.findViewById(R.id.dialog_fragment_edit_user_id_edit_user_id) as EditText
        editUserId.setText(userModel.userEntity?.id)

        val buttonCancel = view.findViewById(R.id.dialog_fragment_edit_user_id_button_cancel) as AppCompatButton
        buttonCancel.setOnClickListener { v ->
            dismiss()
        }

        val buttonOk = view.findViewById(R.id.dialog_fragment_edit_user_id_button_ok) as AppCompatButton
        buttonOk.setOnClickListener { v ->
            val inputtedUserId = editUserId.editableText.toString()
            if (inputtedUserId != userModel.userEntity?.id) {
                userModel.checkAndSaveUserId(getAppContext(), editUserId.editableText.toString())
                showProgressDialog(activity)
            } else {
                dismiss()
            }
        }

        mSubscription = RxTextView.textChanges(editUserId)
                .map { v -> 0 < v.length }
                .subscribe { isEnabled -> buttonOk.toggle(isEnabled) }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSubscription.unsubscribe()
    }

    override fun onResume() {
        super.onResume()
        EventBusHolder.EVENT_BUS.register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBusHolder.EVENT_BUS.unregister(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adjustScreenWidth()
    }

    @Subscribe
    fun subscribe(event: UserIdCheckedEvent) {

        closeProgressDialog()

        val view = view ?: return

        val editUserIdLayout = view.findViewById(R.id.dialog_fragment_edit_user_id_layout_edit_user) as TextInputLayout

        when (event.type) {
            UserIdCheckedEvent.Companion.Type.OK -> {
                editUserIdLayout.isErrorEnabled = false
                dismiss()
            }

            UserIdCheckedEvent.Companion.Type.NG -> {
                editUserIdLayout.error = getString(R.string.message_error_input_user_id)
            }

            UserIdCheckedEvent.Companion.Type.ERROR -> {
                (activity as AppCompatActivity).showSnackbarNetworkError(view)
            }
        }
    }
}
