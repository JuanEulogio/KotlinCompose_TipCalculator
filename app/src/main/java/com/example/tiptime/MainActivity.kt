package com.example.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}

@Composable
fun TipTimeLayout() {
    //TODO: moved here for state hoisting
    // This makes EditNumberField stateless. You hoisted the UI state to its ancestor, TipTimeLayout().
    // The TipTimeLayout() is the state(amountInput) owner now.
    var amountInput by remember { mutableStateOf("") }
    //At the end of the statement, add an ?: Elvis operator that returns a 0.0 value when amountInput is null:
    //The ?: Elvis operator returns the expression that precedes it if the value isn't null and the expression that proceeds it when the value is null.
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    //we use the function here
    val tip = calculateTip(amount)

    Column(
        modifier = Modifier
            //TODO: statusBarsPadding()
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            //TODO: safeDrawingPadding()
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )
        //our made composable
        EditNumberField(
            //added value, and onValueChange for state holding
            value = amountInput,
            onValueChange = { amountInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )
        Text(
            //TODO:
            // note, we get the string with %s where our 'tip' val is passed as a parameter
            // and placed in that string
            text = stringResource(R.string.tip_amount, tip),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(150.dp))
    }
}

/**
 * Calculates the tip based on the user input and format the tip amount
 * according to the local currency.
 * Example would be "$10.00".
 */
private fun calculateTip(amount: Double, tipPercent: Double = 15.0): String {
    // using NumberFormat to display the format of the tip as currency.
    val tip = tipPercent / 100 * amount
    return NumberFormat.getCurrencyInstance().format(tip)
}

//composable inside a composable. This way we can almost customize some composable/widget that
// can be felxible and redone, making less code lines
//
//Note: During initial composition, value in the TextField is set to the initial value, which is an empty string.
//When the user enters text into the text field, the onValueChange lambda callback is called, the lambda executes,
//  and the amountInput.value is set to the updated value entered in the text field.
//The amountInput is the mutable state being tracked by the Compose,
//  recomposition is scheduled. The EditNumberField() composable function is recomposed. Since you
//  are using remember { }, the change survives the recomposition and that is why the state is not re-initialized to "".
//The value of the text field is set to the remembered value of amountInput. The text field
//  recomposes (redrawn on the screen with new value).
@Composable
fun EditNumberField(value: String,
                    onValueChange: (String) -> Unit,
                    modifier: Modifier = Modifier) {

    TextField(
        onValueChange = onValueChange,
        value = value,

        label = { Text(stringResource(R.string.bill_amount)) },
        //only one line, no paragraphs
        singleLine = true,
        //KeyboardType.Number keeps it to numbers
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        //(removed for state hoisting) onValueChange = { amountInput = it },
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipTimeTheme {
        TipTimeLayout()
    }
}
