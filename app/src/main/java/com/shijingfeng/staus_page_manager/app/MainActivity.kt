package com.shijingfeng.staus_page_manager.app

import android.annotation.SuppressLint
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

    @SuppressLint("SetTextI18n")
    private fun initData() {
        val statusPageManager = StatusPageManager.Builder(this)
//            .setEnterAnimator(R.animator.animator_dialog_enter)
//            .setExitAnimator(R.animator.animator_dialog_exit)
            .setDefaultStatusPage(EmptyStatusPage::class.java)
            .setOnStatusPageClickListener { statusPage ->
                Log.e("测试", "onReloadListener ${statusPage::class.java.simpleName}")
            }
            .setOnStatusPageStatusListener(object : OnStatusPageStatusListener {

                /**
                 * 状态页初始化时回调
                 *
                 * @param statusPage 状态页
                 * @param data 数据
                 */
                override fun onInit(statusPage: StatusPage, data: Any?) {
                    super.onInit(statusPage, data)
                    Log.e("测试", "onInit ${statusPage::class.java.simpleName}")
                }

                /**
                 * 状态页被绑定时回调 (当前状态页会被显示)
                 *
                 * @param statusPage 状态页
                 * @param data 数据
                 */
                override fun onShow(statusPage: StatusPage, data: Any?) {
                    super.onShow(statusPage, data)
                    Log.e("测试", "onShow ${statusPage::class.java.simpleName}")
                }

                /**
                 * 状态页更新数据回调 (当前状态页已经显示, 只是更新数据)
                 *
                 * @param statusPage 状态页
                 * @param data 数据
                 */
                override fun onUpdateData(statusPage: StatusPage, data: Any?) {
                    super.onUpdateData(statusPage, data)
                    Log.e("测试", "onUpdateData ${statusPage::class.java.simpleName}   数据: " + (if (data == null) "null" else data as String))
                }

                /**
                 * 状态页被解绑时回调 (当前状态页会被隐藏)
                 *
                 * @param statusPage 状态页
                 */
                override fun onHide(statusPage: StatusPage) {
                    super.onHide(statusPage)
                    Log.e("测试", "onHide ${statusPage::class.java.simpleName}")
                }

            })
            .setDataConvertor { bundle ->
                val type = bundle.getInt("type")

                when (type) {
                    1 -> EmptyStatusPage::class.java
                    2 -> LoadingStatusPage::class.java
                    else -> null
                }
            }
            .build()

        ThreadUtil.runOnUiThread(5000L) {
            statusPageManager.showStatusPage(EmptyStatusPage::class.java, "no data啊")
        }

        // 测试 showStatusPageWithData
//        ThreadUtil.runOnUiThread(5000L) {
//            statusPageManager.showStatusPageWithData(Bundle().apply {
//                putInt("type", 1)
//            })
//            ThreadUtil.runOnUiThread(5000L) {
//                statusPageManager.showStatusPageWithData(Bundle().apply {
//                    putInt("type", 2)
//                })
//                ThreadUtil.runOnUiThread(5000L) {
//                    statusPageManager.showStatusPageWithData(Bundle())
//                }
//            }
//        }

        // 测试 showStatusPage
//        ThreadUtil.runOnUiThread(5000L) {
//            statusPageManager.showStatusPage(EmptyStatusPage::class.java)
//            ThreadUtil.runOnUiThread(5000L) {
//                val currentStatusPage = statusPageManager.currentStatusPage
//                val tvText = currentStatusPage?.view?.findViewById<TextView>(R.id.tv_text)
//
//                tvText?.text = "no data"
//                ThreadUtil.runOnUiThread(5000L) {
//                    statusPageManager.showSuccess()
//                }
//            }
//        }
    }

}