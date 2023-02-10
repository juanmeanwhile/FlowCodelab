package com.meanwhile.flowcodelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.meanwhile.flowcodelab.stage0.UiState
import com.meanwhile.flowcodelab.stage0.ZeroUiState
import com.meanwhile.flowcodelab.stage1.OneUiState
import com.meanwhile.flowcodelab.stage1.Status
import com.meanwhile.flowcodelab.stage2.TwoEndViewModel
import com.meanwhile.flowcodelab.stage2.TwoStartViewModel
import com.meanwhile.flowcodelab.ui.theme.FlowCodelabTheme
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val viewModel: TwoEndViewModel by viewModels()

    @OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FlowCodelabTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                val isRefreshing by remember {
                    derivedStateOf { state.status == Status.LOADING }
                }
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MyScreen(uiState = state, isRefreshing = isRefreshing, viewModel::onRefresh )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyScreen(uiState: UiState, isRefreshing: Boolean, onRefresh: () -> Unit){
    val pullRefreshState = rememberPullRefreshState(isRefreshing , {onRefresh()})
    Box(
        Modifier
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())) {
            Text(
                modifier = Modifier.wrapContentSize(Alignment.Center),
                text = "${(uiState as? OneUiState)?.daysUntilWeekend}\n days until weekend"
            )
        }

        PullRefreshIndicator(isRefreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlowCodelabTheme {
        Greeting("Android")
    }
}