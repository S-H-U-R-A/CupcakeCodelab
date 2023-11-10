package com.example.cupcake.test.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.cupcake.test.onNodeWithStringId
import com.example.cupcake.ui.SelectOptionScreen
import com.example.cupcake.ui.theme.CupcakeTheme
import com.example.cupcake.R
import com.example.cupcake.data.DataSource
import com.example.cupcake.data.OrderUiState
import com.example.cupcake.ui.OrderSummaryScreen
import com.example.cupcake.ui.StartOrderScreen
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Cupcake order screen test
 *
 * Esta clase se usa para hacer test sobre la pantalla de opciones; enfocada en la pantalla de sabores
 *
 *
 */
class CupcakeOrderScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun give_startScreen_when_isHere_then_verifyContent(){
        // Dado una lista de opciones
        val options = DataSource.quantityOptions

        //Cuando la pantalla de inicio es cargada
        composeTestRule.setContent { //implicitamente el método pasa por todos los composables anteriores a este
            CupcakeTheme {
                StartOrderScreen(
                    quantityOptions = options,
                    onNextButtonClicked = {}
                )
            }
        }

        //Entonces verificamos que se muestre cada uno de los elementos de la lista
        options.forEach {  quantityOptions ->
            composeTestRule.onNodeWithStringId(quantityOptions.first).assertIsDisplayed()
        }
    }

    @Test
    fun give_summaryScreen_when_isHere_then_verifyContent(){
        //Dado un fake del estado inicial de los datos
        val fakeOrderUiState = OrderUiState(
            quantity = 6,
            flavor = "Vanilla",
            date = "Mon Nov 13",
            price = "$100",
            pickupOptions = listOf()
        )

        //Cuando la pantalla de resumen esta lista
        composeTestRule.setContent {
            CupcakeTheme {
                OrderSummaryScreen(
                    orderUiState =  fakeOrderUiState ,
                    onCancelButtonClicked = { },
                    onSendButtonClicked = { _, _ ->  }
                )
            }
        }

        //Entonces validamos que los elementos del estado se muestren correctamente
        composeTestRule.also {
            it.onNodeWithText(  fakeOrderUiState.flavor  ).assertIsDisplayed()
            it.onNodeWithText( fakeOrderUiState.date ).assertIsDisplayed()

            it.onNodeWithText(
                it.activity.getString(R.string.subtotal_price, fakeOrderUiState.price)
            ).assertIsDisplayed()
        }

    }

    @Test
    fun give_selectOptionScreen_when_isHere_then_verifyContent(){

        // Dado una lista de opciones
        val flavors = listOf("Vanilla", "Chocolate", "Hazelnut", "Cookie", "Mango")

        // Y un valor de subtotal
        val subtotal = "$100"

        //Cuando la pantalla de seleccion es cargada
        composeTestRule.setContent { //implicitamente el método pasa por todos los composables anteriores a este
            CupcakeTheme {
                SelectOptionScreen(
                    subtotal = subtotal,
                    options = flavors
                )
            }
        }

        //Entonces verificamos que se muestre cada uno de los elementos de la lista
        flavors.forEach { flavor ->
            composeTestRule.onNodeWithText(flavor).assertIsDisplayed()
        }

        //Y entonces verificamos que se muestre el subtotal que le pasamos
        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.subtotal_price,
                subtotal
            )
        ).assertIsDisplayed()

        //Y entonces verificamos que el botón siguiente este deshabilitado
        composeTestRule.onNodeWithStringId(R.string.next).assertIsNotEnabled()


        //Cuando selecionamos un sabor
        composeTestRule.onNodeWithText(flavors.first()).performClick()

        //Entonces verificamos que el botón siguiente este habilitado
        composeTestRule.onNodeWithStringId(R.string.next).assertIsEnabled()

    }


    private fun getFormattedDate(): String {

        val calendar = Calendar.getInstance() // Obtenemos una instancia del calendario.
        calendar.add(
            Calendar.DATE,
            1
        ) // Agregamos un dia a la fecha actual que nos retorna la instancia.

        val formatter =
            SimpleDateFormat("E MMM d", Locale.getDefault())//Creamos un formato para la fecha.

        return formatter.format(calendar.time) //Formateamos la fecha.

    }


}