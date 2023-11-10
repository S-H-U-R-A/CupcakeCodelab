package com.example.cupcake.test

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.cupcake.CupcakeApp
import com.example.cupcake.CupcakeScreen
import com.example.cupcake.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CupcakeScreenNavigationTest {

    @get:Rule
    val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<ComponentActivity>, ComponentActivity> =
        createAndroidComposeRule<ComponentActivity>() // Se crea una regla para las pruebas de UI Compose

    private lateinit var navController: TestNavHostController // Se crea un controlador de navegación para las pruebas

    @Before
    fun setupCupcakeNavHost() {
        composeTestRule.setContent { // Se establece el contenido de la prueba, en este caso la aplicación
            navController =
                TestNavHostController(LocalContext.current).apply { // Creamos un controlador de navegación para las pruebas
                    this.navigatorProvider.addNavigator(ComposeNavigator())
                }
            CupcakeApp(
                navController = navController //Enviamos el controlador de navegación de prueba, para que la aplicación use este controlador de prueba
            )
        }
    }

    @Test
    fun give_cupcakeNavHost_when_startNavigation_then_verifyStartDestination() {
        navController.assertCurrentRouteName(CupcakeScreen.Start.name) //Método de extensión que verifica si la ruta pasada es la actual
    }

    @Test
    fun give_cupcakeNavHost_when_startNavigation_then_verifyNotDisplayedBackArrow() {

        val backText: String =
            composeTestRule.activity.getString(R.string.back_button) //Obtenemos el texto de la descripción de la flecha de retroceso, que se usa en el AppBar

        composeTestRule.onNodeWithContentDescription(backText)//Obtenemos el composable Icon mediante el contenido de la descripción
            .assertDoesNotExist()//Si no existe un nodo con ese texto este método retorna true


    }

    @Test
    fun give_cupcakeNavHost_when_clickOneCupcake_then_navigatesToSelectFlavorScreen() {
        composeTestRule.onNodeWithStringId(R.string.one_cupcake)
            .performClick()

        navController.assertCurrentRouteName(CupcakeScreen.Flavor.name)
    }

    @Test
    fun give_cupcakeNavHost_when_clickNextOnFlavorScreen_then_navigatesToPickupScreen() {
        navigateToFlavorScreen() //Estando en la pantalla de inicio, navegamos a la pantalla de la sabores
        composeTestRule.onNodeWithStringId(R.string.next)
            .performClick() //Hace click en el botón de siguiente
        navController.assertCurrentRouteName(CupcakeScreen.Pickup.name) //Verifica que despues de pulsar el botón naveguemos a la pantalla de seleecion de la fecha
    }

    @Test
    fun cupcakeNavHost_clickBackOnFlavorScreen_navigatesToStartOrderScreen() {
        navigateToFlavorScreen() //Estando en la pantalla de inicio, navegamos a la pantalla de la sabores
        performNavigateUp() //Navegamos hacia atrás
        navController.assertCurrentRouteName(CupcakeScreen.Start.name) //Verificamos que volvemos a la pantalla de inicio
    }

    @Test
    fun cupcakeNavHost_clickCancelOnFlavorScreen_navigatesToStartOrderScreen() {
        navigateToFlavorScreen()//Estando en la pantalla de inicio, navegamos a la pantalla de la sabores
        composeTestRule.onNodeWithStringId(R.string.cancel)
            .performClick() //Hacemos click en el boton de cancelar, que debería navegar a la pantalla de inicio
        navController.assertCurrentRouteName(CupcakeScreen.Start.name) // Validamos que estemos en la pantalla de inicio
    }

    @Test
    fun cupcakeNavHost_clickNextOnPickupScreen_navigatesToSummaryScreen() {
        navigateToPickupScreen()//Estando en la pantalla de inicio, navegamos a la pantalla de selecion de la fecha
        composeTestRule.onNodeWithText( getFormattedDate() )
            .performClick() //Escogemos una opción de fecha
        composeTestRule.onNodeWithStringId(R.string.next)
            .performClick() //Hacemos click en el botón siguiente
        navController.assertCurrentRouteName(CupcakeScreen.Summary.name) //verificamos que estemos en la pantalla de resumen
    }

    @Test
    fun cupcakeNavHost_clickBackOnPickupScreen_navigatesToFlavorScreen() {
        navigateToPickupScreen()
        performNavigateUp()
        navController.assertCurrentRouteName(CupcakeScreen.Flavor.name)
    }

    @Test
    fun cupcakeNavHost_clickCancelOnPickupScreen_navigatesToStartOrderScreen() {
        navigateToPickupScreen()
        composeTestRule.onNodeWithStringId(R.string.cancel)
            .performClick()
        navController.assertCurrentRouteName(CupcakeScreen.Start.name)
    }

    @Test
    fun cupcakeNavHost_clickCancelOnSummaryScreen_navigatesToStartOrderScreen() {
        navigateToSummaryScreen()
        composeTestRule.onNodeWithStringId(R.string.cancel)
            .performClick()
        navController.assertCurrentRouteName(CupcakeScreen.Start.name)
    }


    /*Métodos auxiliares*/

    /**
     * Navigate to flavor screen
     *
     * Método que navega a la pantalla de sabores estando en la pantalla inicio
     * Configura lo necesario para poder navegar a otras pantallas
     *
     */
    private fun navigateToFlavorScreen() { //Método que hace el proceso de click en un botón para navegar a la pantalla de sabores
        composeTestRule.onNodeWithStringId(R.string.one_cupcake)
            .performClick() //Hace click en la pantalla de inicio sobre el botón
        composeTestRule.onNodeWithStringId(R.string.chocolate)
            .performClick() //Selecciona un sabor en las opciones de esta pantalla
    }

    /**
     * Navigate to pickup screen
     *
     * Método que navega a la pantalla de seleción de la fecha estando en la pantalla de sabores
     *
     */
    private fun navigateToPickupScreen() {
        navigateToFlavorScreen()
        composeTestRule.onNodeWithStringId(R.string.next)
            .performClick()
    }

    /**
     * Navigate to summary screen
     *
     * Método que navega a la pantalla de resumen estando en la pantalla de selecion de la fecha
     *
     */
    private fun navigateToSummaryScreen() {
        navigateToPickupScreen()
        composeTestRule.onNodeWithText(getFormattedDate())
            .performClick()
        composeTestRule.onNodeWithStringId(R.string.next)
            .performClick()
    }

    /**
     * Perform navigate up
     *
     * Método que hacle click en la flecha de retroceso
     *
     */
    private fun performNavigateUp() {
        val backText =
            composeTestRule.activity.getString(R.string.back_button) //Obtenemos el string que se usa en el content description del icono de la flecha de retroceso.
        composeTestRule.onNodeWithContentDescription(backText)
            .performClick()//hacemos click en la flecha de retroceso
    }

    /**
     * Get formatted date
     *
     * @return retorna una fecha en formato String, con el fin de buscar un composable que contenga este valor
     */
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