package com.shijingfeng.staus_page_manager.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.shijingfeng.status_page_manager.StatusPageManager
import com.shijingfeng.status_page_manager.listener.OnStatusPageStatusListener
import com.shijingfeng.status_page_manager.status_page.StatusPage
import com.shijingfeng.status_page_manager.util.ThreadUtil
import com.shijingfeng.staus_page_manager.app.status_page.EmptyStatusPage
import com.shijingfeng.staus_page_manager.app.status_page.LoadingStatusPage

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
    }

    private fun initData() {
        val statusPageManager = StatusPageManager.Builder(this)
            .setOnReloadListener { statusPage ->
                Log.e("测试", "onReloadListener ${statusPage::class.java.simpleName}")
            }
            .setOnStatusPageStatusListener(object : OnStatusPageStatusListener {

                /**
                 * 状态页初始化时回调
                 *
                 * @param statusPage 状态页
                 * @param data 数据
                 */
                override fun onInit(statusPage: StatusPage, data: Bundle?) {
                    super.onInit(statusPage, data)
                    Log.e("测试", "onInit ${statusPage::class.java.simpleName}")
                }

                /**
                 * 状态页被绑定时回调 (当前状态页会被显示)
                 *
                 * @param statusPage 状态页
                 * @param data 数据
                 */
                override fun onAttach(statusPage: StatusPage, data: Bundle?) {
                    super.onAttach(statusPage, data)
                    Log.e("测试", "onAttach ${statusPage::class.java.simpleName}")
                }

                /**
                 * 状态页被解绑时回调 (当前状态页会被隐藏)
                 *
                 * @param statusPage 状态页
                 */
                override fun onDetach(statusPage: StatusPage) {
                    super.onDetach(statusPage)
                    Log.e("测试", "onDetach ${statusPage::class.java.simpleName}")
                }

            })
            .setDefaultStatusPage(LoadingStatusPage::class.java)
            .build()

        ThreadUtil.runOnUiThread(5000L) {
            statusPageManager.showStatusPage(EmptyStatusPage::class.java)
            ThreadUtil.runOnUiThread(5000L) {
                statusPageManager.showSuccess()
            }
        }
    }

}