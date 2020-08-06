package pl.kamilbaranowski.chatapp.model.row

import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import pl.kamilbaranowski.chatapp.R
import pl.kamilbaranowski.chatapp.model.User

class UserItem(val user: User): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_textView_user_row_new_message.text = user.username
    }

}