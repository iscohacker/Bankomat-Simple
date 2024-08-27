package uz.iskandarbek.bankomat

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import uz.iskandarbek.bankomat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val etAmount = findViewById<EditText>(R.id.edt_miqdor)
        val btnNext = findViewById<MaterialButton>(R.id.yechish)

        btnNext.setOnClickListener {
            if (etAmount.text.toString().isEmpty()) {
                Toast.makeText(this, "Iltimos, summa kiriting", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val qirq = etAmount.text.substring(etAmount.text.length - 3, etAmount.text.length)
            if (qirq == "000") {


                val amount = etAmount.text.toString().toIntOrNull()

                if (amount != null && amount > 5000 && amount <= 1000000) {
                    val intent = Intent(this, SumActivity::class.java)
                    intent.putExtra("amount", amount)
                    etAmount.text.clear()
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this,
                        "Mablag' standartdan ko'p yoki kam",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else Toast.makeText(this, "To'liq kiritng", Toast.LENGTH_SHORT).show()
        }
    }
}