
package com.example.cupcake

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cupcake.data.DataSource
import com.example.cupcake.data.DataSource.flavors
import com.example.cupcake.ui.OrderSummaryScreen
import com.example.cupcake.ui.OrderViewModel
import com.example.cupcake.ui.SelectOptionScreen
import com.example.cupcake.ui.StartOrderScreen


enum class CupcakeScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Flavor(title = R.string.choose_flavor),
    Pickup(title = R.string.choose_pickup_date),
    Summary(title = R.string.order_summary)
}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@Composable
fun CupcakeAppBar(
    currentScreen: CupcakeScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(id = currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun CupcakeApp(
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    //SE OBTIENE LA PANTALLA ACTUAL DE LA PILA DE PANTALLAS
    val backStackEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()

    //SE OBTIENE EL NOMBRE DE LA PANTALLA ACTUAL
    val currentScreen = CupcakeScreen.valueOf(
        backStackEntry?.destination?.route ?: CupcakeScreen.Start.name
    )

    Scaffold(
        topBar = {
            CupcakeAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null, //Si existe una pantalla anterior en la pila se debe mostrar la flecha de volver a atras
                navigateUp = { navController.navigateUp() }// Cuando hacen click en la flecha ejecutará la acción de volver a atras
            )
        }
    ) { innerPadding ->

        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.Start.name,
            modifier = modifier.padding(innerPadding)
        ){
            //LOS COMPOSABLES QUE SE CREAN EN ESTA PARTE, HARÁN PARTE DE LO QUE SE CONOCE COMO NAV GRAPH
            composable(
                route = CupcakeScreen.Start.name //Este es el nombre de una ruta
            ){
                StartOrderScreen(//En esta parte se debe de poner el composable que se va a mostrar
                    quantityOptions = DataSource.quantityOptions,
                    onNextButtonClicked = { countCupcake ->
                        viewModel.setQuantity(countCupcake) //Actualizamos el estado de la UI administrado por el viewModel
                        navController.navigate(
                            CupcakeScreen.Flavor.name
                        )
                    }
                )
            }

            composable(
                route = CupcakeScreen.Flavor.name //Este es el nombre de una ruta
            ){

                val context: Context = LocalContext.current //Contexto de la aplicación para

                SelectOptionScreen(//En esta parte se debe de poner el composable que se va a mostrar
                    subtotal = uiState.price,
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Pickup.name)
                    },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(
                            viewModel = viewModel,
                            navController = navController
                        )
                    },
                    options = flavors.map { idString ->
                        stringResource(id = idString)
                    },
                    onSelectionChanged = { it ->
                        viewModel.setFlavor(it)
                    }
                )
            }

            composable(
                route = CupcakeScreen.Pickup.name
            ){
                SelectOptionScreen(
                    subtotal = uiState.price,
                    onNextButtonClicked = {
                        navController.navigate(CupcakeScreen.Summary.name)
                    },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(
                            viewModel = viewModel,
                            navController = navController
                        )
                    },
                    options = uiState.pickupOptions,
                    onSelectionChanged = { it -> viewModel.setDate(it) }
                )
            }

            composable(
                route = CupcakeScreen.Summary.name
            ){
                val context: Context = LocalContext.current
                OrderSummaryScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(
                            viewModel = viewModel,
                            navController = navController
                        )
                    },
                    onSendButtonClicked = { subject: String, summary: String ->
                        shareOrder(
                            context,
                            subject,
                            summary
                        )
                    }
                )
            }

        }

    }
}

private fun shareOrder(
    context: Context,
    subject: String,
    summary: String
) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }

    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.new_cupcake_order)
        )
    )

}

private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(
        CupcakeScreen.Start.name,
        inclusive = false
    )
}
