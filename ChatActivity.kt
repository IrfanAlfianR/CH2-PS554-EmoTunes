package com.harshRajpurohit.musicPlayer

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class ChatActivity : AppCompatActivity() {

    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatListView: ListView
    private lateinit var chatAdapter: ChatAdapter
    private val MODEL_ASSETS_PATH = "modelV2.tflite"
    private lateinit var result_text: TextView

    // Max Length of input sequence. The input shape for the model will be ( None , INPUT_MAXLEN ).
    private val INPUT_MAXLEN = 50

    private var tfLiteInterpreter : Interpreter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        chatListView = findViewById(R.id.chatListView)
        result_text = findViewById(R.id.result_text)


        // Inisialisasi adapter
        chatAdapter = ChatAdapter(this)
        chatListView.adapter = chatAdapter

//        val classifier = Classifier( this , "tokenizerV2.json" , INPUT_MAXLEN )
//        // Init TFLiteInterpreter
//        tfLiteInterpreter = Interpreter(loadModelFile())
//
//        // Start vocab processing, show a ProgressDialog to the user.
//        val progressDialog = ProgressDialog( this )
//        progressDialog.setMessage( "Parsing tokenizerV2.json ..." )
//        progressDialog.setCancelable( false )
//        progressDialog.show()
//        classifier.processVocab( object: Classifier.VocabCallback {
//            override fun onVocabProcessed() {
//                // Processing done, dismiss the progressDialog.
//                progressDialog.dismiss()
//            }
//        })


        sendButton.setOnClickListener {
            val userMessage = messageEditText.text.toString().toLowerCase().trim()
            if ( !TextUtils.isEmpty( userMessage ) ) {
//                val tokenizedMessage = classifier.tokenize(message)
//                val paddedMessage = classifier.padSequence(tokenizedMessage)
//
//                val results = classifySequence( paddedMessage )
//                val class1 = results[0]
//                val class2 = results[1]
//                result_text.text = "SPAM : $class2\nNOT SPAM : $class1 "
                // Tambahkan pesan ke daftar chat
                chatAdapter.addMessage(Message("user", userMessage))

                val botResponse = generateResponse(userMessage)
                addBotMessage(botResponse)
                // Bersihkan input teks setelah mengirim pesan
                messageEditText.text.clear()
            }
//            else{
//                Toast.makeText( this@ChatActivity, "Please enter a message.", Toast.LENGTH_LONG).show();
//            }
        }
    }
    private fun generateResponse(userMessage: String): String {
        val lowercaseMessage = userMessage.toLowerCase()

        return when {
            lowercaseMessage.contains("hello") || lowercaseMessage.contains("hi") ->
                "Hello! How can I assist you?"

            lowercaseMessage.contains("I'm sad give me a happy song title") ->
                "Well I'll give you the song title IM NAYEON!"

            lowercaseMessage.contains("bye") ->
                "Goodbye! Have a great day."

            else ->
                "Well I'll give you the song title IM NAYEON!"
        }
    }

    private fun addBotMessage(message: String) {
        chatAdapter.addMessage(Message("bot", message))
    }
//    @Throws(IOException::class)
//    private fun loadModelFile(): MappedByteBuffer {
//        val assetFileDescriptor = assets.openFd(MODEL_ASSETS_PATH)
//        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
//        val fileChannel = fileInputStream.channel
//        val startOffset = assetFileDescriptor.startOffset
//        val declaredLength = assetFileDescriptor.declaredLength
//        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//    }
//
//    // Perform inference, given the input sequence.
//    private fun classifySequence (sequence : IntArray ): FloatArray {
//        // Input shape -> ( 1 , INPUT_MAXLEN )
//        val inputs : Array<FloatArray> = arrayOf( sequence.map { it.toFloat() }.toFloatArray() )
//        // Output shape -> ( 1 , 2 ) ( as numClasses = 2 )
//        val outputs : Array<FloatArray> = arrayOf( FloatArray( 2 ) )
//        tfLiteInterpreter?.run( inputs , outputs )
//        return outputs[0]
//    }

}