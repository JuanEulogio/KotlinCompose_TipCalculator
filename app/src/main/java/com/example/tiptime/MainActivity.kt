package com.example.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import kotlin.math.round

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


    var tipAmountInput by remember { mutableStateOf("") }
    val tipAmount = tipAmountInput.toDoubleOrNull() ?: 0.0


    //TODO: remember about 'remember' and 'observe with mutableStateOf'
    var roundUp by remember { mutableStateOf(false) }





    //TODO: we use the function here and
    // keep track of tip up top here
    val tip = calculateTip(amount, tipAmount, roundUp)

    Column(
        modifier = Modifier
            //TODO: statusBarsPadding()
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            //TODO: safeDrawingPadding()
            .safeDrawingPadding()
            //.verticalScroll(rememberScrollState()) to the modifier to enable the column to scroll vertically.
            // The rememberScrollState() creates and automatically remembers the scroll state.
            .verticalScroll(rememberScrollState()),
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
                .fillMaxWidth(),
            label = R.string.bill_amount,
            //KeyboardType.Number keeps it to numbers, and  keyboard action button is a button
            // at the end of the keyboard:
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            leadingIcon = R.drawable.money
        )
        EditNumberField(
            //added value, and onValueChange for state holding
            value = tipAmountInput,
            onValueChange = { tipAmountInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth(),
            label = R.string.tipPercentage,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            leadingIcon = R.drawable.percent
        )
        RoundTheTipRow(
            roundUp = roundUp,
            //NOTE: how we hoist in the clickback
            onRoundUpChanged = { roundUp = it },
            //NOTE: we premake a padding for it here
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Text(
            //NOTE:
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
//NOTE how tipPercent parameter doesnt have to be initialized.
private fun calculateTip(amount: Double, tipPercent: Double = 15.0, roundTip: Boolean): String {
    var tip = tipPercent / 100 * amount
    if(roundTip) tip= kotlin.math.ceil(tip)
    //NOTE: using NumberFormat to display the format of the tip as currency.
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
                    modifier: Modifier = Modifier,
                    //NOTE: using label parameter val itself as what we decide. not some if else var flag
                    //To denote that the label parameter thats expected to be a string resource reference,
                    // annotate the function parameter with the @StringRes annotation
                    //(also var is Int not lower case int for parameter)
                    @StringRes label: Int,
                    keyboardOptions: KeyboardOptions,
                    @DrawableRes leadingIcon: Int) {

    TextField(
        onValueChange = onValueChange,
        value = value,

        label = { Text(stringResource(label)) },
        //only one line, no paragraphs
        singleLine = true,

        keyboardOptions = keyboardOptions,

        modifier = modifier,
        //NOTE: method of making icon
        leadingIcon= { Icon(painter = painterResource(id = leadingIcon), null) }
    )
}


@Composable
fun RoundTheTipRow(
    modifier: Modifier = Modifier,
    roundUp: Boolean,
    //The callback to be called when the switch is clicked.
    onRoundUpChanged: (Boolean) -> Unit) {
    Row(
        modifier = modifier
            // child elements' width to the maximum on the screen
            .fillMaxWidth()
            .size(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(text = stringResource(id = R.string.round_up_tip))

        Switch(
            //align the Switch composable to the end of the screen
            modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            checked = roundUp,
            onCheckedChange = onRoundUpChanged,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipTimeTheme {
        TipTimeLayout()
    }
}
