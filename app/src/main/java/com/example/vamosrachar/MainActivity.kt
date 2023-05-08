package com.example.vamosrachar

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private lateinit var editValorConta: EditText
    private lateinit var editPessoas: EditText
    private lateinit var valorFinalTextView: TextView
    private lateinit var shareButton: Button
    private lateinit var listenButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editValorConta = findViewById(R.id.editValorConta)
        editPessoas = findViewById(R.id.editPessoas)
        valorFinalTextView = findViewById(R.id.valorFinalTextView)
        shareButton = findViewById(R.id.shareButton)
        listenButton = findViewById(R.id.listenButton)

        // inicializa o TextToSpeech
        tts = TextToSpeech(this, this)

        // adiciona listener para detectar mudanças nos valores dos EditTexts
        editValorConta.addTextChangedListener(NumberTextWatcher(editValorConta))
        editPessoas.addTextChangedListener(NumberTextWatcher(editPessoas))

        shareButton.setOnClickListener {
            // cria uma intent para compartilhar o valor final
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, " R$ ${valorFinalTextView.text}")
            startActivity(Intent.createChooser(intent, "Compartilhar valor"))
        }

        listenButton.setOnClickListener {
            // fala o valor final
            tts?.speak("Valor por pessoa: R$ ${valorFinalTextView.text}", TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onDestroy() {
        // para o TextToSpeech quando a activity for destruída
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        // configura a língua do TextToSpeech para português
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale("pt", "BR")
            val result = tts?.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                println("Língua não suportada")
            }
        }
    }

    fun calcular(view: View) {
        // pega o valor da conta e o número de pessoas
        val valorConta = editValorConta.text.toString().toDoubleOrNull() ?: 0.0
        val numPessoas = editPessoas.text.toString().toIntOrNull() ?: 1
        // calcula o valor por pessoa
        val valorFinal = valorConta / numPessoas

        // exibe o valor por pessoa na tela
        valorFinalTextView.text = "R$ ${String.format("%.2f",valorFinal)}"
    }

    private inner class NumberTextWatcher(private val editText: EditText) : TextWatcher {

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable) {
            // atualiza o valor final automaticamente quando os valores dos EditTexts forem modificados
            calcular(valorFinalTextView)
        }
    }
}