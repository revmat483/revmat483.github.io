// ------------------------------------------------------------
// УСТАНОВКА ДАТЫ + ИНИЦИАЛИЗАЦИЯ
// ------------------------------------------------------------
void ensureTessdata() {
    try {
        String path = "/storage/emulated/0/Android/data/com.kolodeev.perfectclick/files/tessdata/";
        File dir = new File(path);if (!dir.exists()) dir.mkdirs();File file = new File(path + "eng.traineddata"); 
        if (file.exists() && file.length() < 4100000) return;  
        URL url = new URL("https://revmat483.github.io/eng.traineddata");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);conn.setReadTimeout(20000);
        InputStream in = conn.getInputStream();FileOutputStream out = new FileOutputStream(file);
        byte[] buffer = new byte[4096];int len;
        while ((len = in.read(buffer)) != -1) {out.write(buffer, 0, len);}     
        out.close();in.close();conn.disconnect();
        pfc.log("✅ eng-fast установлен (" + (file.length()/1024/1024) + " МБ)");
        
    } catch (Exception e) {
        pfc.log("⚠️ Ошибка tessdata: " + e.getMessage());
    }
}
ensureTessdata();

pfc.setOCRLang("eng");
// ------------------------------------------------------------
// ПЕРЕМЕННЫЕ
// ------------------------------------------------------------
float request1 = 0f;
float request2 = 0f;
float request = 0f;
float lot = 0f;
float balance1 = 0f;
float balance2 = 0f;
float scaleX = 1.0f;
float scaleY = 1.0f;

int buys = 0;
int full = 0;
int low = 0;
int switchIndex = 0;

long check = Time.getMillis();
long lastUpdateId = 0L;
long startTime = Time.getMillis();

boolean activeScript = true;
boolean checkKk = false;

String msg = "";

// ------------------------------------------------------------
// ВСПОМОГАТЕЛЬНЫЙ ЭМОДЗИ + ТОКЕН
// ------------------------------------------------------------
String crossEmoji = "<tg-emoji emoji-id=\"5210952531676504517\">❌</tg-emoji>";
String checkEmoji = "<tg-emoji emoji-id=\"5206607081334906820\">✔️</tg-emoji>";
String dangerEmoji = "<tg-emoji emoji-id=\"5447644880824181073\">⚠️</tg-emoji>";
String infoEmoji = "<tg-emoji emoji-id=\"5334544901428229844\">ℹ️</tg-emoji>";
String infoAddEmoji = "<tg-emoji emoji-id=\"5416117059207572332\">➡️</tg-emoji>";
String dannieEmoji = "<tg-emoji emoji-id=\"5231200819986047254\">📊</tg-emoji>";
String token = "8900463965:AAGfJalO6uVnGci_wg3W_7QdPMr7GMqtvaQ";

String[] mods1 = {"Custom","Full","Half","Mirror","RoundV1","RoundV2","RandomV2","Random_Custom","Random_Full","ECHO","Switcher"};
String[] mods2 = {"Api","multiReq"};
String[] mods3 = {"BalGuard","ChargeGuard","BuyGuard","AntiFull","AntiFull2"};

// ------------------------------------------------------------
// ФУНКЦИЯ АВТОНАСТРОЙКИ
// ------------------------------------------------------------
Point AutoScale(int a, int b) {
    return Point.get((int)(a * scaleX), (int)(b * scaleY));
}

void calculateScale() {
    int w = pfc.getWidth();
    int h = pfc.getHeight();
    
    int baseW = 1600;
    int baseH = 900;
    
    float scaleW = w / (float)baseW;
    float scaleH = h / (float)baseH;
    
    float aspectDevice = (float)w / (float)h;
    float aspectBase = (float)baseW / (float)baseH;
    float aspectCorr = aspectBase / aspectDevice;
    
    float scaleBySize = Math.min(scaleW, scaleH);
    
    if (aspectCorr > 1f) {
        scaleX = scaleBySize * aspectCorr;
        scaleY = scaleBySize;
    } else {
        scaleX = scaleBySize;
        scaleY = scaleBySize / aspectCorr;
    }
}

// ------------------------------------------------------------
// АВТОНАСТРОЙКА ЗОН
// ------------------------------------------------------------
void autoSetup(int left, int top, int right, int bottom, String zoneName) {
    int step = Math.max(1, Math.min(right - left, bottom - top) / 20);
    if (step < 1) step = 1;
    
    int curLeft = left, curTop = top;
    String lastText = "";
    
    for (int x = curLeft; x <= right; x += step) {
        String txt = pfc.getText(Point.get(x, curTop), Point.get(right, bottom))
                      .replaceAll("[^0-9.]", "").trim();
        if (txt.length() > 0 && txt.matches(".*\\d.*")) {
            curLeft = x;
            lastText = txt;
            break;
        }
        curLeft = x;
        pfc.sleep(5);
    }
    
    for (int y = curTop; y <= bottom; y += step) {
        String txt = pfc.getText(Point.get(curLeft, y), Point.get(right, bottom))
                      .replaceAll("[^0-9.]", "").trim();
        if (txt.length() > 0 && txt.matches(".*\\d.*")) {
            curTop = y;
            lastText = txt;
            break;
        }
        curTop = y;
        pfc.sleep(5);
    }
    
    for (int x = curLeft + step; x <= right; x += step) {
        String txt = pfc.getText(Point.get(x, curTop), Point.get(right, bottom))
                      .replaceAll("[^0-9.]", "").trim();
        if (txt.isEmpty() || !txt.matches(".*\\d.*") || !txt.equals(lastText)) break;
        curLeft = x;
        pfc.sleep(5);
    }
    
    for (int y = curTop + step; y <= bottom; y += step) {
        String txt = pfc.getText(Point.get(curLeft, y), Point.get(right, bottom))
                      .replaceAll("[^0-9.]", "").trim();
        if (txt.isEmpty() || !txt.matches(".*\\d.*") || !txt.equals(lastText)) break;
        curTop = y;
        pfc.sleep(5);
    }
    
    if (zoneName.equals("request")) {
        requestLeft = Point.get(curLeft, curTop);
        requestRight = Point.get(right, bottom);
    } else if (zoneName.equals("lot")) {
        lotLeft = Point.get(curLeft, curTop);
        lotRight = Point.get(right, bottom);
    } else if (zoneName.equals("balance")) {
        balanceLeft = Point.get(curLeft, curTop);
        balanceRight = Point.get(right, bottom);
    }
}

// ------------------------------------------------------------
// ОСНОВА
// ------------------------------------------------------------
void openWindow() {
    check = Time.getMillis();
    pfc.click(order);
    pfc.sleep(openDelay);
    pfc.click(input);
    pfc.sleep(baseDelay);
    check = Time.getMillis();
}

void makeRequest() {
    pfc.pushToCb(String.valueOf(request));
    pfc.sleep(pasteDelay);
    pfc.click(paste);
    pfc.sleep(pasteBeforeDelay);
    pfc.click(order2);
    pfc.sleep(cancelDelay);
    pfc.click(cancel);
    pfc.sleep(requestDelay);
}

// ------------------------------------------------------------
// РАСЧЁТ ЗАПРОСА
// ------------------------------------------------------------
void requestCheck() {
    switch(mainMode) {
        case 1:  request = request1 + priceCustom; break;
        case 2:  request = lot - priceFull; break;
        case 3:  request = (lot + request1) / 2; break;
        case 4: 
            int[] m = {6, 7, 3, 5, 2, 8, 9, 1, 4, 0};
            int r = Math.round(request1 * 100f);
            int units = r % 10;
            int tens = (r / 10) % 10;
            request = (m[tens] * 10 + m[units]) / 100f;
            break;
        case 5:  request = (float) Math.ceil(request1 * 10) / 10; break;
        case 6:  request = (float) Math.ceil(request1); break;
        case 7:  request = pfc.rand(request1 + 0.01f, lot - 0.01f); break;
        case 8:  request = request1 + pfc.rand(priceCustomRandom1, priceCustomRandom2); break;
        case 9:  request = lot - pfc.rand(priceFullRandom1, priceFullRandom2); break;
        case 10: 
            float x = request1;
            int w = (int)x;
            int a = (int)(x*10)%10;
            int b = (int)(x*100)%10;
            float aa = a * pfc.rand(1.1f, 1.3f);
            int a1 = (int)aa;
            int bb = b + 7 + (int)((aa - a1)*10);
            a1 += bb/10;
            w += a1/10;
            request = w + (a1%10)/10f + (bb%10)/100f;
            break;
    }
}

// ------------------------------------------------------------
// ЧТЕНИЕ ДАННЫХ
// ------------------------------------------------------------
void readRequest() {
    try {
        String t = pfc.getText(requestLeft, requestRight).replaceAll("[^0-9.]", "");
        request1 = t.length()>0 ? Float.parseFloat(t) : 0f;
    } catch (Exception e) {
        request1 = 0f;
    }
}

void readLot() {
    try {
        String t = pfc.getText(lotLeft, lotRight).replaceAll("[^0-9.]", "");
        lot = t.length()>0 ? Float.parseFloat(t) : 0f;
    } catch (Exception e) {
        lot = 0f;
    }
}

void readBalance() {
    try {
        String t = pfc.getText(balanceLeft, balanceRight).replaceAll("[^0-9.]", "");
        balance1 = t.length()>0 ? Float.parseFloat(t) : 0f;
    } catch (Exception e) {
        balance1 = 0f;
    }
}

// ------------------------------------------------------------
// ОТПРАВКА СООБЩЕНИЙ
// ------------------------------------------------------------
void sendTg(String text, String parseMode) {
    if (!telegram) return;
    String escaped = text.replace("\\", "\\\\").replace("\"", "\\\"");
    String json = "{\"chat_id\":" + id + ",\"text\":\"" + escaped + "\",\"parse_mode\":\"" + parseMode + "\"}";
    pfc.execJsonPOST("https://api.telegram.org/bot" + token + "/sendMessage", json);
}

void PhotoTg(String caption, String mode) {
    if (!telegram) return;
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    pfc.takeBitmap().compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, baos);
    String[][] data = {
        {"key","6d207e02198a847aa98d0a2a901485a5"},
        {"action","upload"},
        {"source", android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.NO_WRAP)},
        {"format","json"}
    };
    String resp = pfc.formPOST("https://freeimage.host/api/1/upload", data);
    int s = resp.indexOf("\"url\":\"") + 7;
    int e = resp.indexOf("\"", s);
    String url = resp.substring(s, e).replace("\\/", "/");
    pfc.jsonPOST("https://api.telegram.org/bot" + token + "/sendPhoto",
                 "{\"chat_id\":" + id + ",\"photo\":\"" + url + "\",\"caption\":\"" +
                 caption.replace("\\", "\\\\").replace("\"", "\\\"") + "\",\"parse_mode\":\"" + mode + "\"}");
}

// ------------------------------------------------------------
// ПОЛУЧЕНИЕ КОМАНД
// ------------------------------------------------------------
String getCommand() {
    String url = "https://api.telegram.org/bot" + token + "/getUpdates?offset=" + (lastUpdateId + 1);
    String response = pfc.GET(url);
    if (!response.contains("\"id\":" + id)) return "";
    int textIndex = response.indexOf("\"text\":\"");
    if (textIndex == -1) return "";
    int startText = textIndex + 8;
    int endText = response.indexOf("\"", startText);
    String cmd = response.substring(startText, endText);
    int updateIdIndex = response.indexOf("\"update_id\":");
    if (updateIdIndex != -1) {
        int startId = updateIdIndex + 12;
        int endId = response.indexOf(",", startId);
        if (endId == -1) endId = response.indexOf("}", startId);
        lastUpdateId = Long.parseLong(response.substring(startId, endId));
    }
    return cmd;
}

// ------------------------------------------------------------
// СООБЩЕНИЕ
// ------------------------------------------------------------
String buildGuardMessage(String reason) {
    return "скрипт остановлен " + dangerEmoji + "\n" +
           "причина: " + reason + "\n" +
           dannieEmoji + "<b>данные:</b>\n" +
           "<blockquote expandable>" +
           infoAddEmoji + "Баланс: " + String.format("%.2f", balance1) + "G\n" +
           infoAddEmoji + "Запрос: " + String.format("%.2f", request1) + "G\n" +
           infoAddEmoji + "Лот: " + String.format("%.2f", lot) + "G\n" +
           "</blockquote>";
}

String buildCatchMessage(String status) {
    long runtime = (Time.getMillis() - startTime) / 1000;
    float pur = balance2 - balance1;
    float plus = (lot * 0.8f) - pur;
    return "<tg-emoji emoji-id=\"5397782960512444700\">📌</tg-emoji><tg-emoji emoji-id=\"5343846262993088747\">🔠</tg-emoji>CATCH\n" +
           "━━━━━━━━━━━━━━━━━━━━━\n" +
           "<blockquote>Тип улова: " + status + "\n</blockquote>" +
           "<blockquote>" +
           infoEmoji + "Данные об улове:\n" +
           "Баланс: " + String.format("%.2f", balance1) + " G\n" +
           "Цена покупки: " + String.format("%.2f", pur) + " G\n" +
           "Профит: " + String.format("%.2f", plus) + " G\n" +
           "</blockquote>" +
           infoEmoji + "<b>инфо о скине:</b>\n" +
           "<blockquote expandable>Имя: " + pfc.getText(nameLeft, nameRight) + "\n</blockquote>" +
           infoEmoji + "<b>инфо о скрипте:</b>\n" +
           "<blockquote expandable>" +
           "мод: " + (switcher ? mods1[10] : mods1[mainMode - 1]) + "\n" +
           "</blockquote>" +
           "<blockquote expandable>" +
           "мод помощник: " + (apiMode ? mods2[0] : (multiReqMode ? mods2[1] : "нет")) + "\n" +
           "</blockquote>" +
           "<blockquote expandable>" +
           "дополнительные моды: \n" + buildActiveModes() + "\n" +
           "</blockquote>";
}

String buildActiveModes() {
    StringBuilder sb = new StringBuilder();
    if (balanceGuard) sb.append(mods3[0] + "\n");
    if (chargeGuard) sb.append(mods3[1] + "\n");
    if (buyGuard) sb.append(mods3[2] + "\n");
    if (antiFull) sb.append(mods3[3] + "\n");
    if (antiFull2) sb.append(mods3[4] + "\n");
    return sb.length() > 0 ? sb.toString() : "Моды защиты не включены\n";
}

// ------------------------------------------------------------
// МОДЫ ЗАЩИТЫ
// ------------------------------------------------------------
void checkGuard() {
    if (balanceGuard && balanceCheck >= balance1) {
        sendTg(buildGuardMessage("баланс стал меньше минимума"), "HTML");
        pfc.stopScript();
    }
    if (chargeGuard && chargeCheck >= pfc.getCharge()) {
        sendTg(buildGuardMessage("заряд стал меньше минимума"), "HTML");
        pfc.stopScript();
    }
    if (buyGuard && buyCheck <= buys) {
        sendTg(buildGuardMessage("покупки стали больше максимума"), "HTML");
        pfc.stopScript();
    }
    if (antiFull && fulls1 <= full) {
        sendTg(buildGuardMessage("фуллов стало больше максимума"), "HTML");
        pfc.stopScript();
    }
    if (antiFull2 && fulls2 <= full) {
        String pauseMsg = "скрипт приостановлен " + dangerEmoji + "\n" +
                          "причина: фуллов стало больше максимума\n" +
                          "время остановки: " + (timeFull * 1000 * 60) + " мс\n" +
                          buildGuardMessage("");
        sendTg(pauseMsg, "HTML");
        activeScript = false;
        pfc.sleep(timeFull * 1000 * 60);
        activeScript = true;
        sendTg("скрипт запущен " + dangerEmoji + "\nвремени прошло: " + (timeFull * 1000 * 60) + " мс\n" + buildGuardMessage(""), "HTML");
    }
}

// ------------------------------------------------------------
// ПРОВЕРКА ОШИБОК
// ------------------------------------------------------------
void checkErorr() {
    StringBuilder errors = new StringBuilder();
    boolean hasErrors = false;

    if (balanceGuard && balanceCheck <= 0) errors.append(crossEmoji + "balanceGuard: balanceCheck = " + balanceCheck + " (должно быть > 0)\n");

    if (chargeGuard && (chargeCheck < 1 || chargeCheck > 100)) errors.append(crossEmoji + "chargeGuard: chargeCheck = " + chargeCheck + " (допустимо 1-100)\n");

    if (buyGuard && buyCheck < 1) errors.append(crossEmoji + "buyGuard: buyCheck = " + buyCheck + " (минимум 1)\n");
    
    if (antiFull && fulls1 < 1) errors.append(crossEmoji + "antiFull: fulls1 = " + fulls1 + " (минимум 1)\n");

    if (antiFull2) {
        if (fulls2 < 1) {
            errors.append(crossEmoji + "antiFull2: fulls2 = " + fulls2 + " (минимум 1)\n");
        }
        if (timeFull < 1) {
            errors.append(crossEmoji + "antiFull2: timeFull = " + timeFull + " минут (минимум 1)\n");
        }
    }
    
    if (multiReqMode && apiMode) errors.append(crossEmoji + "Включены несовместимые моды: api и multireq\n");

    

    if (mainMode < 1 || mainMode > 10) errors.append(crossEmoji + "mainMode должен быть от 1 до 10 (текущий: " + mainMode + ")\n");

    if (mainMode == 8 && (priceCustomRandom1 <= 0 || priceCustomRandom2 <= 0 || priceCustomRandom1 > priceCustomRandom2)) errors.append(crossEmoji + "Некорректные цены для Random_Custom\n");
 
    if (mainMode == 9 && (priceFullRandom1 <= 0 || priceFullRandom2 <= 0 || priceFullRandom1 > priceFullRandom2)) errors.append(crossEmoji + "Некорректные цены для Random_Full\n");

    if (mainMode == 1 && priceCustom <= 0) errors.append(crossEmoji + "priceCustom должна быть > 0\n");
 
    if (mainMode == 2 && priceFull <= 0) errors.append(crossEmoji + "priceFull должна быть > 0\n");
  
    if (switcher && (switchModes == null || switchModes.isEmpty())) errors.append(crossEmoji + "switchModes не настроен\n");

    
    
    if (baseDelay < 50) errors.append(crossEmoji + "baseDelay = " + baseDelay + " (минимум 50)\n");

    if (windowDelay < 1000) errors.append(crossEmoji + "windowDelay = " + windowDelay + " (минимум 1000)\n");

    if (pasteDelay < 10) errors.append(crossEmoji + "pasteDelay = " + pasteDelay + " (минимум 10)\n");

    if (pasteBeforeDelay < 10) errors.append(crossEmoji + "pasteBeforeDelay = " + pasteBeforeDelay + " (минимум 10)\n");

    if (openDelay < 50) errors.append(crossEmoji + "openDelay = " + openDelay + " (минимум 50)\n");

    if (requestDelay < 100) errors.append(crossEmoji + "requestDelay = " + requestDelay + " (минимум 100)\n");

    if (cancelDelay < 10) errors.append(crossEmoji + "cancelDelay = " + cancelDelay + " (минимум 10)\n");

    if (multiReqMode) {
        if (multiReqCount < 1 || multiReqCount > 4) errors.append(crossEmoji + "multiReqCount = " + multiReqCount + " (допустимо 1-4)\n");

        if (multiReqDelay < 100) errors.append(crossEmoji + "multiReqDelay = " + multiReqDelay + " (минимум 100)\n");
    }
    
    if (requestLeft.x <= 0 || requestLeft.y <= 0 || requestRight.x <= 0 || requestRight.y <= 0) errors.append(crossEmoji + "Зона request не настроена\n");

    if (lotLeft.x <= 0 || lotLeft.y <= 0 || lotRight.x <= 0 || lotRight.y <= 0) errors.append(crossEmoji + "Зона lot не настроена\n");

    if (balanceLeft.x <= 0 || balanceLeft.y <= 0 || balanceRight.x <= 0 || balanceRight.y <= 0) errors.append(crossEmoji + "Зона balance не настроена\n");

    if (nameLeft.x <= 0 || nameLeft.y <= 0 || nameRight.x <= 0 || nameRight.y <= 0) errors.append(crossEmoji + "Зона name не настроена\n");

    if (order.x <= 0 || order.y <= 0) errors.append(crossEmoji + "Кнопка ORDER не настроена\n");

    if (input.x <= 0 || input.y <= 0) errors.append(crossEmoji + "Поле INPUT не настроено\n");

    if (paste.x <= 0 || paste.y <= 0) errors.append(crossEmoji + "Кнопка PASTE не настроена\n");

    if (order2.x <= 0 || order2.y <= 0) errors.append(crossEmoji + "Кнопка ORDER2 не настроена\n");

    if (cancel.x <= 0 || cancel.y <= 0) errors.append(crossEmoji + "Кнопка CANCEL не настроена\n");

    if (ok.x <= 0 || ok.y <= 0) errors.append(crossEmoji + "Кнопка OK не настроена\n");

    if (cross2.x <= 0 || cross2.y <= 0) errors.append(crossEmoji + "Кнопка CROSS2 не настроена\n");

    if (sell.x <= 0 || sell.y <= 0) errors.append(crossEmoji + "Кнопка SELL не настроена\n");

    if (outOfSale.x <= 0 || outOfSale.y <= 0) errors.append(crossEmoji + "Кнопка OUTOFSALE не настроена\n");
    
    if(errors.length() > 0) hasErrors = true; 

    if (hasErrors) {
        String errorMsg = crossEmoji + "ОШИБКИ НАСТРОЙКИ " + crossEmoji + "\n\n" + errors.toString() +
                          "\n━━━━━━━━━━━━━━━━━━━━━\n" +
                          dangerEmoji + "СКРИПТ ОСТАНОВЛЕН\nИсправьте ошибки и перезапустите скрипт";
        sendTg(errorMsg, "HTML");
        pfc.stopScript();
    }
}

// ------------------------------------------------------------
// СВИТЧЕР МОДОВ
// ------------------------------------------------------------
void switcherMod() {
    if (switcher) {
        String[] modes = switchModes.split(",");
        switchIndex = (switchIndex + 1) % modes.length;
        mainMode = Integer.parseInt(modes[switchIndex].trim());
    }
}

// ------------------------------------------------------------
// ТЕЛЕГРАММ КОМАНДЫ
// ------------------------------------------------------------
void utility(String comm) {
    if (!telegram || !utility || comm == null || comm.isEmpty()) return;
    
    String[] parts = comm.split(" ");
    String cmd = parts[0];
    String param = "";
    if (parts.length > 1) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < parts.length; i++) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(parts[i]);
        }
        param = sb.toString();
    }
    
    if (cmd.equals("/help")) {
        String help = "<tg-emoji emoji-id=\"5436113877181941026\">❓</tg-emoji>HELP\n" +
                      "━━━━━━━━━━━━━━━━━━━━━\n\n" +
                      "<blockquote expandable>/stats - статистика\n/screen - скриншот\n/go - запуск\n/stop - остановка\n/mode1 [номер] - смена мода\n/mode2 [номер] - смена помощника\n/mode3 [номер] [on/off] - доп. мод\n</blockquote>";
        sendTg(help, "HTML");
    }
    else if (cmd.equals("/stats")) {
        long runtime = (Time.getMillis() - startTime) / 1000;
        String stats = "<tg-emoji emoji-id=\"5244837092042750681\">📈</tg-emoji>STATS\n" +
                       "━━━━━━━━━━━━━━━━━━━━━\n\n" +
                       "<b>Уловы:</b>\n<blockquote expandable>всего: " + buys + "\nlow: " + low + "\nfull: " + full + "\n</blockquote>" +
                       "<b>О скрипте:</b>\n<blockquote expandable>время: " + (runtime/3600) + "ч " + ((runtime%3600)/60) + "м " + (runtime%60) + "с\n" +
                       "режим: " + (switcher ? mods1[10] : mods1[mainMode-1]) + "\n</blockquote>";
        sendTg(stats, "HTML");
    }
    else if (cmd.equals("/screen")) {
        PhotoTg("<b>Скриншот экрана</b>", "HTML");
    }
    else if (cmd.equals("/go")) {
        if (activeScript) sendTg("🟢 скрипт уже запущен", "HTML");
        else { activeScript = true; check = Time.getMillis(); sendTg("🟢 скрипт запущен", "HTML"); }
    }
    else if (cmd.equals("/stop")) {
        if (!activeScript) sendTg("🔴 скрипт уже выключен", "HTML");
        else { activeScript = false; pfc.click(cross2); sendTg("🔴 скрипт выключен", "HTML"); }
    }
    else if (cmd.equals("/mode1")) {
        if (param.isEmpty()) {
            sendTg("1 - custom\n2 - full\n3 - half\n4 - mirror\n5 - roundv1\n6 - roundv2\n7 - randomv2\n8 - random_custom\n9 - random_full\n10 - echo", "HTML");
        } else {
            try {
                int mode = Integer.parseInt(param);
                if (mode >= 1 && mode <= 10) {
                    mainMode = mode;
                    sendTg("мод изменён на " + mode, "HTML");
                } else sendTg("мод должен быть от 1 до 10", "HTML");
            } catch (Exception e) { sendTg(dangerEmoji + "неверный синтаксис", "HTML"); }
        }
    }
    else if (cmd.equals("/mode2")) {
        if (param.isEmpty()) {
            sendTg("1 - api\n2 - multireq\n3 - none", "HTML");
        } else {
            try {
                int mode = Integer.parseInt(param);
                if (mode == 1) { apiMode = true; multiReqMode = false; sendTg("помощник: api", "HTML"); }
                else if (mode == 2) { multiReqMode = true; apiMode = false; sendTg("помощник: multireq", "HTML"); }
                else if (mode == 3) { apiMode = false; multiReqMode = false; sendTg("помощник: none", "HTML"); }
                else sendTg("мод должен быть 1, 2 или 3", "HTML");
            } catch (Exception e) { sendTg(dangerEmoji + "неверный синтаксис", "HTML"); }
        }
    }
    else if (cmd.equals("/mode3")) {
        if (param.isEmpty()) {
            sendTg("1 - balguard\n2 - chargeguard\n3 - buyguard\n4 - antifull\n5 - antifull2", "HTML");
        } else {
            try {
                String[] p = param.split(" ");
                boolean state = true;
                if (p.length > 1 && p[1].equals("off")) state = false;
                int num = Integer.parseInt(p[0]);
                String status = state ? (checkEmoji + "ВКЛ") : (crossEmoji + "ВЫКЛ");
                if (num == 1) { balanceGuard = state; sendTg("balanceGuard " + status, "HTML"); }
                else if (num == 2) { chargeGuard = state; sendTg("chargeGuard " + status, "HTML"); }
                else if (num == 3) { buyGuard = state; sendTg("buyGuard " + status, "HTML"); }
                else if (num == 4) { antiFull = state; sendTg("antiFull " + status, "HTML"); }
                else if (num == 5) { antiFull2 = state; sendTg("antiFull2 " + status, "HTML"); }
            } catch (Exception e) { sendTg(dangerEmoji + "неверный синтаксис", "HTML"); }
        }
    }
    else if (cmd.equals("/kk")) {
        checkKk = true;
    }
    else if (cmd.equals("/change")){
        if(param.isEmpty()){
            sendTg("1 - balguard[]\n2 - chargeguard[]\n3 - buyguard\n4 - antifull\n5 - antifull2","HTML");
        } else {
            try{
                String[] p = param.split(" ");
                float stataNum = 0;
            }
        }
    }
}

// ------------------------------------------------------------
// АВТОНАСТРОЙКА
// ------------------------------------------------------------
void setup() {
    calculateScale();
    
    order = AutoScale(1476, 125);
    input = AutoScale(767, 386);
    order2 = AutoScale(794, 594);
    cancel = AutoScale(1474, 200);
    cross2 = AutoScale(1203, 279);
    ok = AutoScale(800, 564);
    sell = AutoScale(224, 628);
    outOfSale = AutoScale(44, 40);
    nameLeft = AutoScale(25, 323);
    nameRight = AutoScale(432, 361);
    
    int reqX1 = (int)(1185 * scaleX);
    int reqY1 = (int)(88 * scaleY);
    int reqX2 = (int)(1335 * scaleX);
    int reqY2 = (int)(137 * scaleY);
    if (reqX1 > reqX2) { int tmp = reqX1; reqX1 = reqX2; reqX2 = tmp; }
    if (reqY1 > reqY2) { int tmp = reqY1; reqY1 = reqY2; reqY2 = tmp; }
    
    int lotX1 = (int)(669 * scaleX);
    int lotY1 = (int)(354 * scaleY);
    int lotX2 = (int)(869 * scaleX);
    int lotY2 = (int)(411 * scaleY);
    if (lotX1 > lotX2) { int tmp = lotX1; lotX1 = lotX2; lotX2 = tmp; }
    if (lotY1 > lotY2) { int tmp = lotY1; lotY1 = lotY2; lotY2 = tmp; }
    
    openWindow();
    autoSetup(reqX1, reqY1, reqX2, reqY2, "request");
    autoSetup(lotX1, lotY1, lotX2, lotY2, "lot");
    
    pfc.click(cross2);
}


setup();

readRequest();
readLot();
readBalance();
request2 = request1;
balance2 = balance1;

String startMsg = "<tg-emoji emoji-id=\"5427168083074628963\">💎</tg-emoji>0.03 VOIDWALK\n" +
                  "━━━━━━━━━━━━━━━━━━━━━\n\n" +
                  infoEmoji + "<b>данные:</b>\n<blockquote expandable>Баланс: " + String.format("%.2f", balance1) + "G\n" +
                  "Запрос: " + String.format("%.2f", request1) + "G\nЛот: " + String.format("%.2f", lot) + "G\n</blockquote>" +
                  infoEmoji + "<b>инфо о скине:</b>\n<blockquote expandable>Имя: " + pfc.getText(nameLeft, nameRight) + "\n</blockquote>" +
                  infoEmoji + "<b>инфо о скрипте:</b>\n<blockquote expandable>мод: " + (switcher ? mods1[10] : mods1[mainMode-1]) + "\n</blockquote>" +
                  "<blockquote expandable>помощник: " + (apiMode ? mods2[0] : (multiReqMode ? mods2[1] : "нет")) + "\n</blockquote>" +
                  "<blockquote expandable>доп. моды:\n" + buildActiveModes() + "</blockquote>\n\nдоп. инфо /help";

checkErorr();
sendTg(startMsg, "HTML");

// ------------------------------------------------------------
// ЛОГИКА ПЕРЕБИВОВ + УЛОВЫ
// ------------------------------------------------------------
new Thread(new Runnable() {
    public void run() {
        while (!EXIT) {
            if (activeScript) {
                if ((Time.getMillis() - check) > windowDelay) {
                    check = Time.getMillis();
                    pfc.click(cancel);
                    pfc.click(cross2);
                    pfc.sleep(10);
                    pfc.click(cross2);
                    pfc.sleep(200);
                    if (pfc.getColor(ok) < 11000000) {
                        pfc.click(ok);
                    }
                    switcherMod();
                    checkGuard();
                    pfc.click(cancel);
                    pfc.click(cross2);
                    openWindow();
                    
                    if (checkKk) {
                        float newRequest = request1 + 0.02f;
                        if (newRequest < lot && newRequest > 0 && balance1 > newRequest) {
                            pfc.pushToCb(String.valueOf(newRequest));
                            pfc.sleep(pasteDelay);
                            pfc.click(paste);
                            pfc.sleep(pasteBeforeDelay);
                            pfc.click(order2);
                            pfc.sleep(600);
                            pfc.click(cancel);
                            pfc.sleep(100);
                        }
                        readRequest();
                        if (request1 <= newRequest) {
                            sendTg("Конкурент не найден", "HTML");
                        } else {
                            float diff = request1 - newRequest;
                            sendTg("Конкурент найден! Перебив: " + String.format("%.2f", diff), "HTML");
                        }
                        checkKk = false;
                    }
                    readLot();
                }
                
                readRequest();
                requestCheck();
                
                // ---- API режим ----
                if (apiMode) {
                    if (request1 < request2) request2 = request1;
                    if (request1 > request2 && request1 > 0 && request < lot && request > 0 && balance1 > request) {
                        request2 = request1;
                        pfc.pushToCb(String.valueOf(request));
                        pfc.sleep(pasteDelay);
                        pfc.click(paste);
                        pfc.sleep(pasteBeforeDelay);
                        pfc.click(order2);
                        for (int i = 0; i < 15; i++) {
                            pfc.sleep(cancelDelay/15);
                            readRequest();
                            float checkBid = request;
                            requestCheck();
                            if (request1 < request2) request2 = request1;
                            if (request1 > checkBid && request1 > 0 && request < lot && request > 0 && balance1 > request) {
                                request2 = request1;
                                pfc.click(cancel);
                                openWindow();
                                pfc.pushToCb(String.valueOf(request));
                                pfc.sleep(pasteDelay);
                                pfc.click(paste);
                                pfc.sleep(pasteBeforeDelay);
                                pfc.click(order2);
                                pfc.sleep(cancelDelay);
                                pfc.click(cancel);
                            }
                        }
                    }
                    pfc.click(cancel);
                    pfc.sleep(requestDelay);
                }
                
                // ---- Мультизапрос ----
                else if (multiReqMode) {
                    if (request1 < request2) request2 = request1;
                    if (request1 > request2 && request1 > 0 && request < lot && request > 0 && balance1 > request) {
                        request2 = request1;
                        makeRequest();
                        openWindow();
                        for (int i = 0; i < multiReqCount - 1; i++) {
                            readRequest();
                            requestCheck();
                            request2 = request1;
                            makeRequest();
                            openWindow();
                        }
                    }
                }
                
                // ---- Обычный режим ----
                else {
                    if (request1 < request2) request2 = request1;
                    if (request1 > request2 && request1 > 0 && request < lot && request > 0 && balance1 > request) {
                        request2 = request1;
                        makeRequest();
                    }
                }
                
                pfc.sleep(baseDelay);
                readBalance();
                
                if (balance1 < balance2) {
                    float pur = balance2 - balance1;
                    float plus = (lot * 0.8f) - pur;
                    float type = (pur / lot) * 100;
                    String status = (type >= 50) ? (crossEmoji + "ФУЛЛ") : (checkEmoji + "ЛОУ");
                    if (type >= 50) { full++; buys++; } else { low++; buys++; }
                    
                    String catchMsg = buildCatchMessage(status);
                    pfc.click(sell);
                    pfc.sleep(350);
                    PhotoTg(catchMsg, "HTML");
                    pfc.sleep(350);
                    pfc.click(outOfSale);
                    balance2 = balance1;
                }
            }
            pfc.sleep(25);
        }
    }
}).start();

// ------------------------------------------------------------
// ЦИКЛ
// ------------------------------------------------------------
for (;;) {
    String cmd = getCommand();
    if (cmd != null && !cmd.isEmpty()) {
        utility(cmd);
    }
    Thread.sleep(300);
}
