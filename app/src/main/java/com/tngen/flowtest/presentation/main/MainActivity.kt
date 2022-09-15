package com.tngen.flowtest.presentation.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.tngen.flowtest.R
import com.tngen.flowtest.databinding.ActivityMainBinding
import com.tngen.flowtest.presentation.base.BaseActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.system.measureTimeMillis

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {
    override val viewModel by viewModel<MainViewModel>()

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        lifecycleScope.launchWhenStarted {
            viewModel.sharedFlow.collect {
                Log.d("MainActivity_lifecycle", it.toString())
            }
        }
        initButton()
    }

    override fun initViews() {
        GlobalScope.launch {
            val measureTime = measureTimeMillis {
                val job1 = launch {
                    delay(2000L)
                }
                job1.join()

                val job2 = launch {
                    delay(4000L)
                    println("job2 finished")
                }
            }
            println("measureTime : ${measureTime} ")
        }



    }

    private fun initButton() = with(binding) {
        sharedButton.setOnClickListener {
//            Toast.makeText(this@MainActivity, "dd", Toast.LENGTH_SHORT).show()
            viewModel.changeSharedFlow()
        }
    }


    override fun observeData() {

    }
}