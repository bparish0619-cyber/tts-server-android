package com.github.jing332.tts

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.jing332.database.entities.plugin.Plugin
import com.github.jing332.database.entities.systts.source.PluginTtsSource
import com.github.jing332.tts.synthesizer.SystemParams
import com.github.jing332.tts.speech.plugin.PluginTtsProvider
import com.github.jing332.tts.speech.plugin.engine.TtsPluginUiEngineV2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TtsPluginEngineTest {
    private val context: Context by lazy {
        InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun ttsrv() {
        val code = """
            let token = ttsrv.userVars.token
            console.log(token)
            const PluginJS = {
                'name': 'lingban',
                'id': 'lingban.cn',
                'author': 'jing',
                'version': 2,
                'vars': {
                    token: { label: "Token", hint: "Your token" },
                },
                'onLoad': function() {
                    console.log("onLoad")
                    console.log(ttsrv.userVars)
                },
                
                // Called when there is no getAudio()
                'getAudioV2': function(request, callback){
                    console.log(request)    
                },
                
                'getAudio': function(text, locale, voice, rate, volume, pitch) {
                    let reqUrl = 'https://paas.lingban.cn/tts/v1/audition'
                    let reqHeaders = {
                        'content-type': 'application/json;charset=UTF-8'
                    }
                    let reqBody = {
                        'text': text,
                        'speaker': voice,
                        'volume': volume,
                        'speed': rate,
                        'pitch': pitch
                    }
                    reqBody = JSON.stringify(reqBody)
                    
                    let resp = ttsrv.httpPost(reqUrl, reqBody, reqHeaders)
                    return resp.body().byteStream()
                }
            } 

            let EditorJS = {
                // Called when saving on the Edit TTS screen
                'getAudioSampleRate': function(locale, voice) {
                    let audio = PluginJS.getAudio('test', locale, voice, 50, 50, 50)
                    let rate = ttsrv.getAudioSampleRate(audio)
            
                    return rate
                },
            
                'onLoadData': function() {
            
                },
            
                'getLocales': function() {
                    return ['zh-CN', 'en-US']
                },
            
                'getVoices': function(locale) {
                    console.log(typeof locale)
                    if (locale == 'zh-CN') {
                        return {'yanyan':'Yanyan [friendly, sweet female voice]','lulu':'Lulu [lively, enthusiastic female voice]','qiqi':'Qiqi [fresh, crisp female voice]','nana':'Nana [soft, gentle female voice]','xiaocheng':'Xiaocheng [smooth, songlike female voice]','xiaoyao':'Xiaoyao [sweet, lively female voice]','chenyu':'Chenyu [warm, polite female voice]','lingfeng':'Lingfeng [gentle, charming female voice]','ruirui':'Ruirui [calm, gentle female voice]','luyao':'Luyao [melodious, lingering female voice]','linglong':'Linglong [lively, sweet female voice]','yurou':'Yurou [light, cheerful female voice]','siqi':'Siqi [graceful, melodious female voice]','chenchen':'Chenchen [passionate female voice]','qiulian':'Qiulian [breezy, pleasant female voice]','xintong':'Xintong [clear, pleasant female voice]','yiyi':'Yiyi [clear, melodious female voice]','wenhui':'Wenhui [mellow, polished female voice]','xiaoxiao':'Xiaoxiao [trustworthy, friendly female voice]','xiaofang':'Xiaofang [innocent, lively female voice]','xiaoai':'Xiaoai [sincere, friendly female voice]','nainai':'Nainai [pure, natural female voice]','chengmiao':'Chengmiao [brisk, spirited female voice]','zhengcheng':'Zhengcheng [gentle, firm male voice]','zili':'Zili [quiet, elegant male voice]','ningning':'Ningning [friendly, gentle male voice]','zhentian':'Zhentian [bold, unrestrained male voice]','bufan':'Bufan [magnetic, mellow male voice]','wenxi':'Wenxi [sunny, natural male voice]','tiantian':'Tiantian [steady, deep male voice]','lingyu':'Lingyu [warm, smooth male voice]','wusong':'Wusong [clear, serious male voice]','kunde':'Kunde [deep, steady male voice]','jinyan':'Jinyan [clear, precise male voice]','shenzhi':'Shenzhi [tense, intense male voice]','ruyan':'Ruyan [earnest male voice]','jianming':'Jianming [fantasy novel voice]','yuzhang':'Yuzhang [fantasy novel voice]','chuanqi':'Chuanqi [game novel voice]','lingmeng':'Lingmeng [romance novel voice]','shenghun':'Shenghun [fantasy novel voice]','yizhou':'Yizhou [urban novel voice]','youlan':'Youlan [romance novel voice]','hongmang':'Hongmang [fantasy novel voice]','youran':'Youran [fantasy novel voice]','shanyu':'Shanyu [fantasy novel voice]','mengjing':'Mengjing [urban novel voice]','yunsong':'Yunsong [fantasy novel voice]','haohan':'Haohan [urban novel voice]','linger':'Linger [youthful female voice]','yaoyao':'Yaoyao [youthful male voice]','subo':'Subo [male news voice]','mina':'Mina [female news voice]','linghui':'Linghui [female news voice]','lingyin':'Lingyin [female news voice]','shuangshuang':'Shuangshuang [male news voice]','jeff':'Jeff [English news voice]','rose':'Rose [English novel voice]','julia':'Julia [English novel voice]','jack':'Jack [English novel voice]','liangshu':'Liangshu [celebrity-style voice]','xiaosong':'Xiaosong [celebrity-style voice]','laoyi':'Laoyi [celebrity-style voice]','dafei':'Dafei [celebrity-style voice]','shishen':'Shishen [celebrity-style voice]','wansheng':'Wansheng [celebrity-style voice]','kaige':'Kaige [celebrity-style voice]','guobao':'Guobao [celebrity-style voice]','jiange':'Jiange [celebrity-style voice]'}
                    }
                },
            
                'onLoadUI': function(ctx, linearLayout) {
            
                }
            }
        """.trimIndent()

        val plugin = Plugin(code = code, userVars = mapOf("token" to "value-token"))
        val tte = TtsPluginUiEngineV2(context, plugin)
 //        tte.onLoadData()
//        val locales = tte.getLocales()
        val voices = tte.getVoices("zh-CN")


//        tte.getAudio(
//            text = "The quick brown fox jumps over the lazy dog.",
//            locale = "zh-CN",
//            voice = "test-li-bai",
//            rate = 1.2f,
//            volume = 1.25f,
//            pitch = 1.80f
//        )

    }


    @Test
    fun cancelable() = runBlocking {
        val code = """
        const PluginJS = {
            'name': 'lingban',
            'id': 'lingban.cn',
            'author': 'jing',
            'version': 2,
            'vars': {
                bearer: { label: "token", hint: "Your token" },
            },
            'onLoad': function() {
                console.log("onLoad")
            },
            
            // Called when there is no getAudio()
            'getAudioV2': function(request, callback){
                console.log(request)    
            },
            
            'getAudio': function(text, locale, voice, rate, volume, pitch) {
                let reqUrl = 'https://paas.lingban.cn/tts/v1/audition'
                let reqHeaders = {
                    'content-type': 'application/json;charset=UTF-8'
                }
                let reqBody = {
                    'text': text,
                    'speaker': voice,
                    'volume': volume,
                    'speed': rate,
                    'pitch': pitch
                }
                reqBody = JSON.stringify(reqBody)
                console.log(reqBody)

                java.lang.Thread.sleep(5000)
                let resp = ttsrv.httpPost(reqUrl, reqBody, reqHeaders)
                return resp.body().byteStream()
            }
            
       } 
        """.trimIndent()
        val ttsEngineService = object : PluginTtsProvider(context, Plugin(code = code)) {
        }
        val job = launch(Dispatchers.IO) {
            ttsEngineService.onInit()
            val inputStream = ttsEngineService.getStream(
                SystemParams(text = "Hello, I am androidTest"),
                PluginTtsSource(pluginId = "", locale = "zh-CN", voice = "")
            )
        }
        delay(1000)
        job.cancel()
    }

    @Test
    fun testUiJsImporter() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val code = """
            LinearLayout.VERTICAL
        """.trimIndent()
        val engine = TtsPluginUiEngineV2(context, Plugin(code = code))

     }

}