import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_messenger.ChatLog.ChatLogActivity
import com.example.kotlin_messenger.ChatLog.ChatMessage
import com.example.kotlin_messenger.NewMessages.bindImage
import com.example.kotlin_messenger.R
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private val CHAT_FROM_ME = 0
private val CHAT_FROM_YOU = 1

class ChatAdapter:androidx.recyclerview.widget.ListAdapter<ChatMessage, RecyclerView.ViewHolder>(refershlist())
{ private val adapterScope = CoroutineScope(Dispatchers.Default)
    fun addHeaderAndSubmitList(list:List<ChatMessage>?) {
        adapterScope.launch {
            val items = list
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }
   object Statiscated{ var mContext:Context?=null}

    class ViewHolder private  constructor( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var you_text=itemView.findViewById<TextView>(R.id.you_text)
        var you_pic=itemView.findViewById<ImageView>(R.id.you_profile)
        var send_img_you=itemView.findViewById<ImageView>(R.id.send_img_you)

        public fun bind(item: ChatMessage) {
            val res = itemView.context.resources
            if(item.imageUrl=="")
            {you_text.text=item.text
                Log.d("dead","${item}")
                bindImage(you_pic,item.fromidurl)
                send_img_you.visibility=View.GONE
            }
            else
            {   bindImage(send_img_you,item.imageUrl)
                bindImage(you_pic,item.fromidurl)
                send_img_you.setOnClickListener{
                    Log.d("click","you")
                    downloadImage(item.imageUrl,Statiscated.mContext as Context)
                }
                you_text.visibility=View.GONE
            }
        }

        companion object {
            public fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.chat_from_you, parent, false)
                return ViewHolder(view)
            }
        }
    }
    class TextViewHolder private  constructor(itemview: View): RecyclerView.ViewHolder(itemview) {
        var me_text=itemview.findViewById<TextView>(R.id.me_text)
        var me_pic=itemview.findViewById<ImageView>(R.id.me_profile)
        var send_img_me=itemView.findViewById<ImageView>(R.id.send_img_me)
        public fun bind(item: ChatMessage) {
            val res = itemView.context.resources
            if(item.imageUrl=="")
            {me_text.text=item.text
            Log.d("dead","${item}")
            bindImage(me_pic,item.fromidurl)
                send_img_me.visibility=View.GONE
            }
            else
                {   bindImage(send_img_me,item.imageUrl)
                    bindImage(me_pic,item.fromidurl)
                    send_img_me.setOnClickListener{
                        Log.d("click","me")
                        downloadImage(item.imageUrl,Statiscated.mContext as Context)
                    }
                    me_text.visibility=View.GONE
                }
        }
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.chat_of_me, parent, false)
                return TextViewHolder(view)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CHAT_FROM_ME -> TextViewHolder.from(parent)
            CHAT_FROM_YOU-> ViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val you_chat = getItem(position)
                holder.bind(you_chat)
            }
            is TextViewHolder->{
                val me_chat = getItem(position)
                holder.bind(me_chat)
            }

        }
    }
    override fun getItemViewType(position: Int): Int {
        return when(getItem(position).fromId) {
            FirebaseAuth.getInstance().uid-> CHAT_FROM_ME
            else -> CHAT_FROM_YOU
        }
    }

}
class refershlist: DiffUtil.ItemCallback<ChatMessage>(){
    override fun areItemsTheSame(oldItem:ChatMessage, newItem: ChatMessage): Boolean {
        return oldItem.toId ==newItem.toId
    }
    override fun areContentsTheSame(oldItem: ChatMessage, newItem:ChatMessage): Boolean {
        return oldItem==newItem
    }

}
fun downloadImage(url: String,context: Context) {
    Log.d("function","$url")
    var msg: String? = ""
    var lastMsg = ""
    val directory = File(Environment.DIRECTORY_PICTURES)
    if (!directory.exists()) {
        directory.mkdirs()
    }

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    val downloadUri = Uri.parse(url)

    val request = DownloadManager.Request(downloadUri)
    val downloadId = downloadManager.enqueue(request)
    val query = DownloadManager.Query().setFilterById(downloadId)
    Thread(Runnable {
        var downloading = true
        while (downloading) {
            val cursor: Cursor = downloadManager.query(query)
            cursor.moveToFirst()
            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                downloading = false
            }
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            msg = statusMessage(url, directory, status)
            if (msg != lastMsg) {

                    Log.d("status","$msg")
                lastMsg = msg ?: ""
            }
            cursor.close()
        }
    }).start()
}
fun statusMessage(url: String, directory: File, status: Int): String? {
    var msg = ""
    msg = when (status) {
        DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
        DownloadManager.STATUS_PAUSED -> "Paused"
        DownloadManager.STATUS_PENDING -> "Pending"
        DownloadManager.STATUS_RUNNING -> "Downloading..."
        DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url.substring(
            url.lastIndexOf("/") + 1
        )
        else -> "There's nothing to download"
    }
    return msg
}