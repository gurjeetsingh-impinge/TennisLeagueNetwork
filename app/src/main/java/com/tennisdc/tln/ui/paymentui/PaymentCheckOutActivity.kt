package com.tennisdc.tln.ui.paymentui;


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.tennisdc.tln.BaseDialog
import com.tennisdc.tln.Constants
import com.tennisdc.tln.R
import com.tennisdc.tln.network.WSHandle
import java.util.*

class PaymentCheckOutActivity : AppCompatActivity() {

    private lateinit var cardNumberEditText: EditText
    private lateinit var expirationEditText: EditText
    private lateinit var cvvEditText: EditText
    private lateinit var nameOnCardEditText: EditText
    private lateinit var emailAddressEditText: EditText
    private lateinit var submitButton: Button
    private  lateinit var checkBox: CheckBox
    private  lateinit var amount: TextView
    private lateinit var description : String


    override fun onResume() {
        super.onResume()
        title = "PAYMENT CHECKOUT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
     val  finalCost=  intent?.getDoubleExtra("FinalCost",0.0)
     val  addDesc=  intent?.getSerializableExtra("addDesc")

        cardNumberEditText = findViewById(R.id.payCardNumber)
        expirationEditText = findViewById(R.id.payExpiration)
        cvvEditText = findViewById(R.id.payCvv)
        nameOnCardEditText = findViewById(R.id.payNameOnCard)
        emailAddressEditText = findViewById(R.id.payEmailAddress)
        submitButton = findViewById(R.id.btnSubmitBusinessCard)

        checkBox = findViewById(R.id.payCheckBox)
        amount = findViewById(R.id.amount)
        println("Check Amount $"+ finalCost)
        println("ADD $"+ addDesc)

        amount.text = "$ ${finalCost.toString()}"


        expirationEditText.addTextChangedListener(object : TextWatcher {

            private val dateFormat = "\\d{2}/\\d{2}" // Regular expression for "MM/YY" format

            override fun afterTextChanged(s: Editable?) {
                // No need to implement anything here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                // No need to implement anything here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var working = s.toString()
                var isValid = true
                if (working.length == 2 && before == 0) {
                    if (Integer.parseInt(working) < 1 || Integer.parseInt(working) > 12) {
                        working += "/"
                        expirationEditText.setText(working)
                        expirationEditText.setSelection(working.length)
                        isValid = false
                    } else {
                        working += "/"
                        expirationEditText.setText(working)
                        expirationEditText.setSelection(working.length)
                    }
                } else if (working.length == 5 && before == 0) {
                    val enteredYear = working.substring(3)
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100//getting last 2 digits of current year i.e. 2018 % 100 = 18
                    if (Integer.parseInt(enteredYear) < currentYear) {
                        isValid = false
                    }
                } else if (working.length != 5) {
                    isValid = false
                }

                if (!isValid) {
                    expirationEditText.error = getString(R.string.enter_valid_date_mm_yy)
                } else {
                    expirationEditText.error = null
                }

            }
        })






        submitButton.setOnClickListener {
            if (validatePaymentFields()) {
                updatePaymentCheck()
                // Payment fields are valid, proceed with payment logic
            }
        }
    }

    private fun updatePaymentCheck() {
        val progressDialog = ProgressDialog.show(this, null, "Please wait...")
        val lastAmount = amount.text.toString().replace("$","")
        val finalAmount = lastAmount.toString().replace(".","")
        println("Check ${finalAmount}")


        WSHandle.UpdatePaymentCheckout(
            /* account_number = */
            cardNumberEditText.text.toString(),
            /* expidate = */
            expirationEditText.text.toString().replace("/",""),
            /* accountHolderName = */
            nameOnCardEditText.text.toString(),

            /* transaction_amount = */

            finalAmount.toString(),
            /* notification_email_address = */
            emailAddressEditText.text.toString(),
            description
,
            /* requestListener = */
            object : com.tennisdc.tln.network.VolleyHelper.IRequestListener<String, String?> {
                override fun onFailureResponse(response: String?) {
                    progressDialog.dismiss()
                    val dialog = BaseDialog.SimpleDialog.getDialogInstance(null, 0, response)
                    dialog.show(supportFragmentManager, "dlg-frag")
                }

                override fun onSuccessResponse(response: String) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@PaymentCheckOutActivity,
                        "Payment Done Successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    var var3 : Intent = Intent().putExtra(Constants.EXTRA_RESULT_CONFIRMATION, response)
                        setResult(-1, var3)
                    finish()
                }

                override fun onError(error: VolleyError) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@PaymentCheckOutActivity,
                        "Network Error : " + error.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            )

    }

    private fun validatePaymentFields(): Boolean {
        val cardNumber = cardNumberEditText.text.toString().trim()
        val expiration = expirationEditText.text.toString().trim()
        val cvv = cvvEditText.text.toString().trim()
        val nameOnCard = nameOnCardEditText.text.toString().trim()
        val emailAddress = emailAddressEditText.text.toString().trim()



        // Perform validation for each field
        if (cardNumber.isEmpty()) {
            cardNumberEditText.error = "Card number is required"
            cardNumberEditText.requestFocus()
            return false
        }
        else  if (cardNumber.length < 16) {
            cardNumberEditText.error = "Card number is not valid"
            cardNumberEditText.requestFocus()
            return false
        }
        else if (cardNumber.length > 16){
            cardNumberEditText.error = "Card number is not valid"
            cardNumberEditText.requestFocus()
            return false
        }

        if (expiration.isEmpty()) {

            expirationEditText.error = "Expiration date is required"
            expirationEditText.requestFocus()
            return false
        }

        // Perform additional validation for expiration date format

        if (cvv.isEmpty()) {
            cvvEditText.error = "CVV is required"
            cvvEditText.requestFocus()
            return false
        }else if (cvv.length >3){
            cvvEditText.error = "CVV is not valid "
            cvvEditText.requestFocus()
            return false
        } else if (cvv.length<3){
            cvvEditText.error = "CVV is not valid "
            cvvEditText.requestFocus()
            return false
        }

        // Perform additional validation for CVV format

        if (nameOnCard.isEmpty()) {
            nameOnCardEditText.error = "Name on card is required"
            nameOnCardEditText.requestFocus()
            return false
        }


        if (!checkBox.isChecked) {
            // Checkbox is not checked
            Toast.makeText(this, "Please click on check box", Toast.LENGTH_SHORT).show()
            return false
        }

//        if (emailAddress.isEmpty()) {
//            emailAddressEditText.error = "Email address is required"
//            emailAddressEditText.requestFocus()
//            return false
//        }

        // Perform additional validation for email address format

        return true
    }
}

