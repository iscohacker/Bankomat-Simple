package uz.iskandarbek.bankomat

import android.app.ProgressDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.iskandarbek.bankomat.databinding.ActivitySumBinding

class SumActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySumBinding.inflate(layoutInflater) }
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val tvAmount = binding.tvAmount
        val tvBillDetails = binding.tvBillDetails
        val flBills = findViewById<FrameLayout>(R.id.flBills)

        progressDialog = ProgressDialog(this).apply {
            setMessage("Iltimos kuting...")
            setCancelable(false)
            show()
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.money)
        mediaPlayer.start()

        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.dismiss()
            mediaPlayer.release()
            startBillAnimation(tvAmount, tvBillDetails, flBills)
        }, 4000)
    }

    private fun startBillAnimation(
        tvAmount: TextView,
        tvBillDetails: TextView,
        flBills: FrameLayout
    ) {
        val amount = intent.getIntExtra("amount", 0)
        tvAmount.text = "Kiritilgan summa: $amount so'm"

        val billCounts = calculateBills(amount)

        val usedBillsText = StringBuilder()
        var offset = 0


        for ((billValue, count) in billCounts) {
            if (count > 0) {
                usedBillsText.append("$count dona $billValue so'mlik\n")
            }
        }

        tvBillDetails.text = usedBillsText.toString()

        val mainScope = MainScope()

        mainScope.launch {
            for ((billValue, count) in billCounts) {
                if (count > 0) {
                    for (i in 0 until count) {
                        val imageView = ImageView(this@SumActivity).apply {
                            val resourceId = getBillImageResource(billValue.toString())
                            setImageResource(resourceId)
                            translationY = offset.toFloat()
                        }

                        flBills.addView(imageView)
                        offset += 60

                        // Animatsiyani ishlatish
                        withContext(Dispatchers.Main) {
                            val anim = AnimationUtils.loadAnimation(
                                this@SumActivity,
                                android.R.anim.slide_in_left
                            )
                            imageView.startAnimation(anim)
                        }

                        delay(200)
                    }
                }
            }
        }
    }

    private fun calculateBills(amount: Int): Map<Int, Int> {
        val bills = listOf(100000, 50000, 10000, 5000, 1000)
        val billCounts = mutableMapOf<Int, Int>()

        var remainingAmount = amount

        for (bill in bills) {
            if (remainingAmount >= bill) {
                val count = remainingAmount / bill
                remainingAmount %= bill
                billCounts[bill] = count
            }
        }

        if (remainingAmount > 0) {
            billCounts[1] = remainingAmount
        }

        return billCounts
    }

    private fun getBillImageResource(billName: String): Int {
        return when (billName) {
            "100000" -> R.drawable.yuz
            "50000" -> R.drawable.ellik
            "10000" -> R.drawable.on
            "5000" -> R.drawable.besh
            "1000" -> R.drawable.bir
            else -> 0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
