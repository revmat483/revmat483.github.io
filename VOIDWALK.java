String crossEmoji = "<tg-emoji emoji-id=\"5210952531676504517\">❌</tg-emoji>";
String checkEmoji = "<tg-emoji emoji-id=\"5206607081334906820\">✔️</tg-emoji>";
String dangerEmoji = "<tg-emoji emoji-id=\"5447644880824181073\">⚠️</tg-emoji>";
String infoEmoji = "<tg-emoji emoji-id=\"5334544901428229844\">ℹ️</tg-emoji>";
String infoAddEmoji = "<tg-emoji emoji-id=\"5416117059207572332\">➡️</tg-emoji>";
String dannieEmoji = "<tg-emoji emoji-id=\"5231200819986047254\">📊</tg-emoji>";



float request1 = 0f;
float request2 = 0f;
float request = 0f;
float lot = 0f;
float balance1 = 0f;
float balance2 = 0f;



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

String[] mods1 = {"Custom","Full","Half","Mirror","RoundV1","RoundV2","RandomV2","Random_Custom","Random_Full","ECHO","Switcher"};

String[] mods2 = {"Api","multiReq"};

String[] mods3 = {"BalGuard","ChargeGuard","BuyGuard","AntiFull","AntiFull2"};

String ActivePerebiv = switcher?mods1[10]:mods1[mainMode - 1];

String ActiveHelpPerebiv = apiMode?mods2[0]:(multiReqMode?mods2[1]:"нет включённых модов помощников"); 

StringBuilder tgmod = new StringBuilder();
if(balanceGuard) tgmod.append(mods3[1]+"\n");
if(chargeGuard) tgmod.append(mods3[2]+"\n");
if(buyGuard) tgmod.append(mods3[3]+"\n");
if(antiFull) tgmod.append(mods3[4]+"\n");
if(antiFull2) tgmod.append(mods3[5]+"\n");
String activeModes = tgmod.length() > 0 ? tgmod.toString() : "Моды защиты не включены\n";

String token = "8900463965:AAGfJalO6uVnGci_wg3W_7QdPMr7GMqtvaQ";
     
if(settings){
    int wight=pfc.getWidth();
    int hight=pfc.getHeight();
    double kWidth=wight/1600.;
    double kHeight=hight/900.;
    order = Point.get((int)(kWidth * 1476),(int)(kHeight * 125));
    input = Point.get((int)(kWidth * 767),(int)(kHeight * 386));
    order2 = Point.get((int)(kWidth * 794),(int)(kHeight * 594));
    cancel = Point.get((int)(kWidth * 1474),(int)(kHeight * 200));
    cross2 = Point.get((int)(kWidth * 1203),(int)(kHeight * 279));
    ok = Point.get((int)(kWidth * 800),(int)(kHeight * 564));
    sell = Point.get((int)(kWidth * 224),(int)(kHeight * 628));
    outOfSale = Point.get((int)(kWidth * 44),(int)(kHeight * 40));
    nameLeft = Point.get((int)(kWidth * 25),(int)(kHeight * 323));
    nameRight = Point.get((int)(kWidth * 432),(int)(kHeight * 361));
}

pfc.startCapture(2);
pfc.setOCRLang("eng");


void autoSetup(int left, int top, int right, int bottom, String zoneName) {
    int step = Math.max(1, Math.min(right - left, bottom - top) / 20);
    if (step < 1) step = 1;
    
    int curLeft = left, curTop = top;
    String lastText = "";
    
    for (int x = curLeft; x <= right; x += step) {
        String txt = pfc.getText(Point.get(x, curTop), Point.get(right, bottom)).replaceAll("[^0-9.]", "").trim();
        if (txt.length() > 0 && txt.matches(".*\\d.*")) { curLeft = x; lastText = txt; break; }
        curLeft = x;
        pfc.sleep(5);
    }
    
    for (int y = curTop; y <= bottom; y += step) {
        String txt = pfc.getText(Point.get(curLeft, y), Point.get(right, bottom)).replaceAll("[^0-9.]", "").trim();
        if (txt.length() > 0 && txt.matches(".*\\d.*")) { curTop = y; lastText = txt; break; }
        curTop = y;
        pfc.sleep(5);
    }
    
    for (int x = curLeft + step; x <= right; x += step) {
        String txt = pfc.getText(Point.get(x, curTop), Point.get(right, bottom)).replaceAll("[^0-9.]", "").trim();
        if (txt.isEmpty() || !txt.matches(".*\\d.*") || !txt.equals(lastText)) break;
        curLeft = x;
        pfc.sleep(5);
    }
    
    for (int y = curTop + step; y <= bottom; y += step) {
        String txt = pfc.getText(Point.get(curLeft, y), Point.get(right, bottom)).replaceAll("[^0-9.]", "").trim();
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




void openWindow(){
    check = Time.getMillis();
    pfc.click(order);
    pfc.sleep(openDelay);
    pfc.click(input);
    pfc.sleep(baseDelay);
    check = Time.getMillis();
    
}



void makeRequest(){
    pfc.pushToCb(String.valueOf(request));
    pfc.sleep(pasteDelay);pfc.click(paste);pfc.sleep(pasteBeforeDelay);
    pfc.click(order2);
    pfc.sleep(cancelDelay);pfc.click(cancel);
    pfc.sleep(requestDelay);
}

void requestCheck(){
    switch(mainMode) {
        case 1:  
            request = request1 + priceCustom;
            break;

        case 2: 
            request = lot - priceFull;
            break;

        case 3:  
            request = (lot + request1) / 2;
            break;

        case 4:
            int[] m = {6, 7, 3, 5, 2, 8, 9, 1, 4, 0};
            int r = Math.round(request1 * 100f);
            int units = r % 10;        
            int tens = (r / 10) % 10;  
            int newUnits = m[units];
            int newTens = m[tens];
            request = (newTens * 10 + newUnits) / 100f;
            break;

        case 5:  
            request = (float) Math.ceil(request1 * 10) / 10;
            break;

        case 6:  
            request = (float) Math.ceil(request1);
            break;

        case 7:
            request = pfc.rand(request1 + 0.01f, lot - 0.01f);
            break;

        case 8:
            request = request1 + pfc.rand(priceCustomRandom1, priceCustomRandom2);
            break;

        case 9:
            request = lot - pfc.rand(priceFullRandom1, priceFullRandom2);
            break;

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

void msgGuard(String prichina){
    msg = 
    "скрипт остоновлен" + dangerEmoji + "\n" +
    "причина: " + prichina + "\n" +
    dannieEmoji + "<b>данные:</b>" + "\n" +
    "<blockquote expandable>" +
    infoAddEmoji + "Баланс:" +
    String.format("%.2f",balance1) + "G" + "\n" +
    infoAddEmoji + "Запрос:" + 
    String.format("%.2f",request1) + "G" + "\n" +
    infoAddEmoji + "Лот:" + 
    String.format("%.2f",lot) + "G" + "\n" +
    "</blockquote>"; 
}
void checkGuard(){
    
    if(balanceGuard && balanceCheck >= balance1){
        msgGuard("баланс стал меньше минимума");
        sendTg(msg, "HTML");
        pfc.stopScript();
    }

    if(chargeGuard && chargeCheck >= pfc.getCharge()){
        msgGuard("заряд стал меньше минимума");
        sendTg(msg, "HTML");
        pfc.stopScript();
    }

    if(buyGuard && buyCheck <= buys){
        msgGuard("покупки стали больше максимума");
        sendTg(msg, "HTML");
        pfc.stopScript();
    }

    if(antiFull && fulls1 <= full){
        msgGuard("фуллов стало больше максимума");
        sendTg(msg, "HTML");
        pfc.stopScript();
    }

    if(antiFull2 && fulls2 <= full){
        msg = 
        "скрипт приостоновлен" + dangerEmoji + "\n" +
        "причина: фуллов стало больше максимума\n" +
        "время остановки" + (timeFull * 1000 * 60) + "\n" +
        dannieEmoji + "<b>данные:</b>" + "\n" +
        "<blockquote expandable>" +
        infoAddEmoji + "Баланс:" +
        String.format("%.2f",balance1) + "G" + "\n" +
        infoAddEmoji + "Запрос:" + 
        String.format("%.2f",request1) + "G" + "\n" +
        infoAddEmoji + "Лот:" + 
        String.format("%.2f",lot) + "G" + "\n" +
        "</blockquote>";
        sendTg(msg, "HTML");
        activeScript = false;
        pfc.sleep(timeFull * 1000 * 60);
        activeScript = true;
        msg = 
        "скрипт запущен" + dangerEmoji + "\n" +
        "времени прошло" + (timeFull * 1000 * 60) + "\n" +
        dannieEmoji + "<b>данные:</b>" + "\n" +
        "<blockquote expandable>" +
        infoAddEmoji + "Баланс:" +
        String.format("%.2f",balance1) + "G" + "\n" +
        infoAddEmoji + "Запрос:" + 
        String.format("%.2f",request1) + "G" + "\n" +
        infoAddEmoji + "Лот:" + 
        String.format("%.2f",lot) + "G" + "\n" +
        "</blockquote>";
        sendTg(msg, "HTML");
    }
}



void switcherMod(){
    if(switcher) {
        String[] modes = switchModes.split(",");
        switchIndex = (switchIndex + 1) % modes.length;
        int newMode = Integer.parseInt(modes[switchIndex].trim());
        mainMode = newMode;
    }
}



void sendTg(String text, String parse_mode) {
    if(!telegram) return;
    String escaped = text.replace("\\", "\\\\").replace("\"", "\\\"");
    String json = "{\"chat_id\":" + id + ",\"text\":\"" + escaped + "\",\"parse_mode\":\"" + parse_mode + "\"}";
    pfc.execJsonPOST("https://api.telegram.org/bot" + token + "/sendMessage", json);
}



void PhotoTg(String caption, String mode) {
    if(!telegram) return;
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    pfc.takeBitmap().compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, baos);
    String[][] data = {{"key","6d207e02198a847aa98d0a2a901485a5"},{"action","upload"},{"source",android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.NO_WRAP)},{"format","json"}};
    String resp = pfc.formPOST("https://freeimage.host/api/1/upload", data);
    int s = resp.indexOf("\"url\":\"") + 7, e = resp.indexOf("\"", s);
    String url = resp.substring(s, e).replace("\\/", "/");
    pfc.jsonPOST("https://api.telegram.org/bot" + token + "/sendPhoto", "{\"chat_id\":" + id + ",\"photo\":\"" + url + "\",\"caption\":\"" + caption.replace("\\", "\\\\").replace("\"", "\\\"") + "\",\"parse_mode\":\"" + mode + "\"}");
}



String getCommand() {
    String url = "https://api.telegram.org/bot" + token + "/getUpdates?offset=" + (lastUpdateId + 1);
    String response = pfc.GET(url);
    if(!response.contains("\"id\":" + id)) return "";   
    int textIndex = response.indexOf("\"text\":\"");
    if(textIndex == -1) return "";   
    int startText = textIndex + 8;
    int endText = response.indexOf("\"", startText);
    String cmd = response.substring(startText, endText);
    int updateIdIndex = response.indexOf("\"update_id\":");
    if(updateIdIndex != -1) {
        int startId = updateIdIndex + 12;
        int endId = response.indexOf(",", startId);
        if(endId == -1) endId = response.indexOf("}", startId);
        lastUpdateId = Long.parseLong(response.substring(startId, endId));
    }
    
    return cmd;
}



void checkErorr(){
    StringBuilder errors = new StringBuilder();
    boolean hasErrors = false;
    
     if(balanceGuard){
        if(balanceCheck <= 0){
            errors.append(crossEmoji + "balanceGuard: balanceCheck = ").append(balanceCheck).append("\n");
            errors.append("Значение должно быть больше 0\n");
            errors.append("Рекомендуемое значение: 0.5 - 10.0\n\n");
            hasErrors = true;
        }        
    }
    
    if(chargeGuard){
        if(chargeCheck < 1 || chargeCheck > 100){
            errors.append(crossEmoji + "chargeGuard: chargeCheck = ").append(chargeCheck).append("\n");
            errors.append("Допустимые значения: от 1 до 100\n");
            errors.append("Рекомендуемое значение: 10-20\n\n");
            hasErrors = true;
        }
    }
    
    if(buyGuard){
        if(buyCheck < 1){
            errors.append(crossEmoji + "buyGuard: buyCheck = ").append(buyCheck).append("\n");
            errors.append("Минимальное значение: 1\n");
            errors.append("Установите количество покупок\n\n");
            hasErrors = true;
        }
    }
    
    if(antiFull){
        if(fulls1 < 1){
            errors.append(crossEmoji + "antiFull: fulls1 = ").append(fulls1).append("\n");
            errors.append("Минимальное значение: 1\n");
            errors.append("Установите количество фуллов\n\n");
            hasErrors = true;
        }
    }
    
    if(antiFull2){
        if(fulls2 < 1){
            errors.append(crossEmoji + "antiFull2: fulls2 = ").append(fulls2).append("\n");
            errors.append("Минимальное значение: 1\n");
            errors.append("Установите количество фуллов\n\n");
            hasErrors = true;
        }    
        if(timeFull < 1){
            errors.append(crossEmoji + "antiFull2: timeFull = ").append(timeFull).append(" минут\n");
            errors.append("Минимальное значение: 1 минута\n");
            errors.append("Рекомендуемое значение: 5-30 минут\n\n");
            hasErrors = true;
        }
    }

    if(multiReqMode && apiMode){
        errors.append(crossEmoji +"Включены не совместимые моды: api и multireq\n");
        errors.append("Выключите один из них!\n\n");
        hasErrors = true;
    }
    
    if(mainMode < 1 || mainMode > 10){
        errors.append(crossEmoji + "Основной мод должен быть от 1 до 10\n");
        errors.append("Текущее значение: ").append(mainMode).append("\n\n");
        hasErrors = true;
    }
    
    
    if(baseDelay < 50){
        errors.append(crossEmoji +"baseDelay = ").append(baseDelay).append(" (минимум 50)\n");
        errors.append("Установите значение 50 или выше\n\n");
        hasErrors = true;
    }
    
    if(windowDelay < 1000){
        errors.append(crossEmoji + "windowDelay = ").append(windowDelay).append(" (минимум 1000)\n");
        errors.append("Установите значение 1000 или выше\n\n");
        hasErrors = true;
    }
    
    if(pasteDelay < 10){
        errors.append(crossEmoji + "pasteDelay = ").append(pasteDelay).append(" (минимум 10)\n");
        errors.append("Установите значение 10 или выше\n\n");
        hasErrors = true;
    }
    
    if(pasteBeforeDelay < 10){
        errors.append(crossEmoji + "pasteBeforeDelay = ").append(pasteBeforeDelay).append(" (минимум 10)\n");
        errors.append("Установите значение 10 или выше\n\n");
        hasErrors = true;
    }
    
    if(openDelay < 50){
        errors.append(crossEmoji + "openDelay = ").append(openDelay).append(" (минимум 50)\n");
        errors.append("Установите значение 50 или выше\n\n");
        hasErrors = true;
    }
    
    if(requestDelay < 100){
        errors.append(crossEmoji + "requestDelay = ").append(requestDelay).append(" (минимум 100)\n");
        errors.append("Установите значение 100 или выше\n\n");
        hasErrors = true;
    }
    
    if(cancelDelay < 10){
        errors.append(crossEmoji + "cancelDelay = ").append(cancelDelay).append(" (минимум 10)\n");
        errors.append("Установите значение 10 или выше\n\n");
        hasErrors = true;
    }
    
    if(multiReqMode){
        if(multiReqCount < 1 || multiReqCount > 4){
            errors.append(crossEmoji + "multiReqCount = ").append(multiReqCount).append(" (допустимо 1-4)\n");
            errors.append("Установите значение от 1 до 4\n\n");
            hasErrors = true;
        }
        
        if(multiReqDelay < 100){
            errors.append(crossEmoji + "multiReqDelay = ").append(multiReqDelay).append(" (минимум 100)\n");
            errors.append("Установите значение 100 или выше\n\n");
            hasErrors = true;
        }
    }
    
    if(balanceGuard){
        if(balanceCheck <= 0){
            errors.append(crossEmoji + "balanceCheck = ").append(balanceCheck).append(" (должно быть больше 0)\n");
            errors.append("Установите положительное значение\n\n");
            hasErrors = true;
        }
    }
    
    if(chargeGuard){
        if(chargeCheck < 1 || chargeCheck > 100){
            errors.append(crossEmoji + "chargeCheck = ").append(chargeCheck).append(" (допустимо 1-100)\n");
            errors.append("Установите значение от 1 до 100\n\n");
            hasErrors = true;
        }
    }
    
    if(buyGuard){
        if(buyCheck < 1){
            errors.append(crossEmoji + "buyCheck = ").append(buyCheck).append(" (минимум 1)\n");
            errors.append("Установите значение 1 или выше\n\n");
            hasErrors = true;
        }
    }
    
    if(antiFull){
        if(fulls1 < 1){
            errors.append(crossEmoji + "fulls1 = ").append(fulls1).append(" (минимум 1)\n");
            errors.append("Установите значение 1 или выше\n\n");
            hasErrors = true;
        }
    }
    
    if(antiFull2){
        if(fulls2 < 1){
            errors.append(crossEmoji + "fulls2 = ").append(fulls2).append(" (минимум 1)\n");
            errors.append("Установите значение 1 или выше\n\n");
            hasErrors = true;
        }
        
        if(timeFull < 1){
            errors.append(crossEmoji + "timeFull = ").append(timeFull).append(" (минимум 1 минута)\n");
            errors.append("Установите значение 1 или выше\n\n");
            hasErrors = true;
        }
    }
    
    if(mainMode == 8){
        if(priceCustomRandom1 <= 0 || priceCustomRandom2 <= 0){
            errors.append(crossEmoji + "Цены для Random_Custom не настроены!\n");
            errors.append("priceCustomRandom1 = ").append(priceCustomRandom1).append("\n");
            errors.append("priceCustomRandom2 = ").append(priceCustomRandom2).append("\n");
            errors.append("Установите положительные значения\n\n");
            hasErrors = true;
        }
        
        if(priceCustomRandom1 > priceCustomRandom2){
            errors.append(crossEmoji + "priceCustomRandom1 больше priceCustomRandom2\n");
            errors.append("priceCustomRandom1 = ").append(priceCustomRandom1).append("\n");
            errors.append("priceCustomRandom2 = ").append(priceCustomRandom2).append("\n");
            errors.append("Первое значение должно быть меньше второго\n\n");
            hasErrors = true;
        }
    }
    
    if(mainMode == 9){
        if(priceFullRandom1 <= 0 || priceFullRandom2 <= 0){
            errors.append(crossEmoji + "Цены для Random_Full не настроены!\n");
            errors.append("priceFullRandom1 = ").append(priceFullRandom1).append("\n");
            errors.append("priceFullRandom2 = ").append(priceFullRandom2).append("\n");
            errors.append("Установите положительные значения\n\n");
            hasErrors = true;
        }
        
        if(priceFullRandom1 > priceFullRandom2){
            errors.append(crossEmoji + "priceFullRandom1 больше priceFullRandom2\n");
            errors.append("priceFullRandom1 = ").append(priceFullRandom1).append("\n");
            errors.append("priceFullRandom2 = ").append(priceFullRandom2).append("\n");
            errors.append("Первое значение должно быть меньше второго\n\n");
            hasErrors = true;
        }
    }
    
    if(mainMode == 1 && priceCustom <= 0){
        errors.append(crossEmoji + "priceCustom = ").append(priceCustom).append(" (должна быть больше 0)\n");
        errors.append("Установите положительное значение\n\n");
        hasErrors = true;
    }
    
    if(mainMode == 2 && priceFull <= 0){
        errors.append(crossEmoji + "priceFull = ").append(priceFull).append(" (должна быть больше 0)\n");
        errors.append("Установите положительное значение\n\n");
        hasErrors = true;
    }
    
    if(switcher){
        if(switchModes == null || switchModes.isEmpty()){
            errors.append(crossEmoji + "switchModes не настроен!\n");
            errors.append("Укажите режимы через запятую (например: 1,2,3)\n\n");
            hasErrors = true;
        }
    }
    

    if(requestLeft.x <= 0 || requestLeft.y <= 0 || requestRight.x <= 0 || requestRight.y <= 0){
        errors.append(crossEmoji + "Зона запроса (request) не настроена!\n");
        errors.append("left(").append(requestLeft.x).append(",").append(requestLeft.y);
        errors.append(") right(").append(requestRight.x).append(",").append(requestRight.y).append(")\n\n");
        hasErrors = true;
    }
    
    if(lotLeft.x <= 0 || lotLeft.y <= 0 || lotRight.x <= 0 || lotRight.y <= 0){
        errors.append(crossEmoji + "Зона лота (lot) не настроена!\n");
        errors.append("left(").append(lotLeft.x).append(",").append(lotLeft.y);
        errors.append(") right(").append(lotRight.x).append(",").append(lotRight.y).append(")\n\n");
        hasErrors = true;
    }
    
    if(balanceLeft.x <= 0 || balanceLeft.y <= 0 || balanceRight.x <= 0 || balanceRight.y <= 0){
        errors.append(crossEmoji + "Зона баланса (balance) не настроена!\n");
        errors.append("left(").append(balanceLeft.x).append(",").append(balanceLeft.y);
        errors.append(") right(").append(balanceRight.x).append(",").append(balanceRight.y).append(")\n\n");
        hasErrors = true;
    }
    
    if(nameLeft.x <= 0 || nameLeft.y <= 0 || nameRight.x <= 0 || nameRight.y <= 0){
        errors.append(crossEmoji + "Зона имени скина (name) не настроена!\n");
        errors.append("left(").append(nameLeft.x).append(",").append(nameLeft.y);
        errors.append(") right(").append(nameRight.x).append(",").append(nameRight.y).append(")\n\n");
        hasErrors = true;
    }
    
    if(order.x <= 0 || order.y <= 0){
        errors.append(crossEmoji + "Кнопка ORDER не настроена!\n");
        errors.append("Координаты: ").append(order.x).append(",").append(order.y).append("\n\n");
        hasErrors = true;
    }
    
    if(input.x <= 0 || input.y <= 0){
        errors.append(crossEmoji + "Поле INPUT не настроено!\n");
        errors.append("Координаты: ").append(input.x).append(",").append(input.y).append("\n\n");
        hasErrors = true;
    }
    
    if(paste.x <= 0 || paste.y <= 0){
        errors.append(crossEmoji + "Кнопка PASTE не настроена!\n");
        errors.append("   Координаты: ").append(paste.x).append(",").append(paste.y).append("\n\n");
        hasErrors = true;
    }

    if(order2.x <= 0 || order2.y <= 0){
        errors.append(crossEmoji + "Кнопка ORDER2 не настроена!\n");
        errors.append("   Координаты: ").append(order2.x).append(",").append(order2.y).append("\n\n");
        hasErrors = true;
    }
    
    if(cancel.x <= 0 || cancel.y <= 0){
        errors.append(crossEmoji + "Кнопка CANCEL не настроена!\n");
        errors.append("Координаты: ").append(cancel.x).append(",").append(cancel.y).append("\n\n");
        hasErrors = true;
    }
        
    if(ok.x <= 0 || ok.y <= 0){
        errors.append(crossEmoji + "Кнопка OK не настроена!\n");
        errors.append("Координаты: ").append(ok.x).append(",").append(ok.y).append("\n\n");
        hasErrors = true;
    }
    
    if(cross2.x <= 0 || cross2.y <= 0){
        errors.append(crossEmoji + "Кнопка CROSS2 не настроена!\n");
        errors.append("Координаты: ").append(cross2.x).append(",").append(cross2.y).append("\n\n");
        hasErrors = true;
    }
    
    if(sell.x <= 0 || sell.y <= 0){
        errors.append(crossEmoji + "Кнопка SELL не настроена!\n");
        errors.append("Координаты: ").append(sell.x).append(",").append(sell.y).append("\n\n");
        hasErrors = true;
    }
    
    if(outOfSale.x <= 0 || outOfSale.y <= 0){
        errors.append(crossEmoji + "Кнопка OUTOFSALE не настроена!\n");
        errors.append("Координаты: ").append(outOfSale.x).append(",").append(outOfSale.y).append("\n\n");
        hasErrors = true;
    }
    
    if(hasErrors){
        String errorMsg = crossEmoji + "ОШИБКИ НАСТРОЙКИ " + crossEmoji + "\n\n" + errors.toString();
        errorMsg += "\n━━━━━━━━━━━━━━━━━━━━━\n";
        errorMsg += dangerEmoji + "СКРИПТ ОСТАНОВЛЕН\n";
        errorMsg += "Исправьте ошибки и перезапустите скрипт";
        
        sendTg(errorMsg, "HTML");
        pfc.stopScript();
    }
}

openWindow();

void readRequest(){
    try{
        String t = pfc.getText(requestLeft, requestRight).replaceAll("[^0-9.]", "");
        request1 = t.length()>0?Float.parseFloat(t) : 0f;
    }
    catch (Exception e){
        request1 = 0f;
    }
}
void readLot(){
    try{
        String t = pfc.getText(lotLeft, lotRight).replaceAll("[^0-9.]", "");
        lot = t.length()>0?Float.parseFloat(t) : 0f;
    }
    catch (Exception e){
        lot = 0f;
    }
}
void readBalance(){
    try{
        String t = pfc.getText(balanceLeft, balanceRight).replaceAll("[^0-9.]", "");
        balance1 = t.length()>0?Float.parseFloat(t) : 0f;
    }
    catch (Exception e){
        balance1 = 0f;
    }
}
if(settings){
    int wight=pfc.getWidth();
    int hight=pfc.getHeight();
    double kWidth=wight/1600.;
    double kHeight=hight/900.;
    autoSetup((int)(kWidth * 1185),(int)(kHeight * 88),(int)(kWidth * 1335),(int)(kHeight * 137), "request");  
    autoSetup((int)(kWidth * 669),(int)(kHeight * 354),(int)(kWidth * 869),(int)(kHeight * 411), "lot");
    autoSetup((int)(kWidth * 1345),(int)(kHeight * 15),(int)(kWidth * 1519) ,(int)(kHeight * 57), "balance");      
}
readRequest();
readLot();
readBalance();
request2 = request1;
balance2 = balance1;


long runtime = (Time.getMillis() - startTime) / 1000;
String Startmsg =
"<tg-emoji emoji-id=\"5427168083074628963\">💎</tg-emoji><tg-emoji emoji-id=\"5413729723110959206\">🔤</tg-emoji><tg-emoji emoji-id=\"5411379190589068657\">🔤</tg-emoji><tg-emoji emoji-id=\"5413804236498574929\">🔤</tg-emoji><tg-emoji emoji-id=\"5413570770666298683\">🔤</tg-emoji><tg-emoji emoji-id=\"5411308242024308443\">🔤</tg-emoji><tg-emoji emoji-id=\"5413565539396132646\">❤️</tg-emoji><tg-emoji emoji-id=\"5411231271915396616\">🔤</tg-emoji><tg-emoji emoji-id=\"5413507480028224243\">🔤</tg-emoji><tg-emoji emoji-id=\"5427168083074628963\">💎</tg-emoji>\n" +
"━━━━━━━━━━━━━━━━━━━━━\n\n" +


infoEmoji + "<b>данные:</b>" + "\n" +
"<blockquote expandable>" +
"Баланс:" + String.format("%.2f",balance1) + "G" + "\n" +
"Запрос:" + String.format("%.2f",request1) + "G" + "\n" +
"Лот:" + String.format("%.2f",lot) + "G" + "\n" +
"</blockquote>" + 

infoEmoji + "<b>инфо о скине:</b>" + "\n" +
"<blockquote expandable>" + 
"Имя:" + pfc.getText(nameLeft,nameRight) + "\n" +
"</blockquote>" + 


infoEmoji + "<b>инфо о скрипте:</b>" + "\n" +
"<blockquote expandable>" +
"<tg-emoji emoji-id=\"5334544901428229844\">ℹ️</tg-emoji>" + "мод" + "\n" +
ActivePerebiv + "\n" +
"</blockquote>" + 

"<blockquote expandable>" +
"<tg-emoji emoji-id=\"5334544901428229844\">ℹ️</tg-emoji>" + "мод помощник перебива" + "\n" +
ActiveHelpPerebiv + "\n" +
"</blockquote>" + 

"<blockquote expandable>" +
"<tg-emoji emoji-id=\"5334544901428229844\">ℹ️</tg-emoji>" + "дополнительные моды" + "\n" +
activeModes + "\n" +
"</blockquote>" + "\n" +

"дополнительная инфорамация /help"
;
checkErorr();
sendTg(Startmsg, "HTML");
new Thread(new Runnable(){
    public void run(){
        while(!EXIT){
            if(activeScript){          
                if((Time.getMillis() - check) > windowDelay){
                    check = Time.getMillis();
                    pfc.click(cancel);
                    pfc.click(cross2);
                    pfc.sleep(10);
                    pfc.click(cross2);
                    pfc.sleep(200);
                    if(pfc.getColor(ok) < 11000000){
                        pfc.click(ok);
                    }
                    switcherMod();
                    checkGuard();
                    pfc.click(cancel);
                    pfc.click(cross2);
                    openWindow();
                    if(checkKk){
                        float newRequest = request1 + 0.02f;
                        if(newRequest < lot && newRequest > 0 && balance1 > newRequest){
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
                        if(request1 <= newRequest){
                            sendTg("❌ Конкурент не найден", "HTML");
                        }
                        else {
                            float diff = request1 - newRequest;
                            sendTg("⚠️ Конкурент найден!\nЕго перебив: " + String.format("%.2f", diff), "HTML");
                        }
                        checkKk = false; 
                    }
                readLot();
                }
                readRequest();
                requestCheck();
                if(apiMode){
                    if(request1 < request2){
                        request2 = request1;
                    }
                    if(request1 > request2 && request1 > 0 && request < lot && request > 0 && balance1 > request){
                        request2 = request1;
                        pfc.pushToCb(String.valueOf(request));
                        pfc.sleep(pasteDelay);
                        pfc.click(paste);
                        pfc.sleep(pasteBeforeDelay);
                        pfc.click(order2);
                    }
                    for(int i = 0; i < 15; i++){
                        pfc.sleep(cancelDelay/15);
                        readRequest();
                        float checkBid = request;
                        requestCheck();
                        if(request1 < request2){
                            request2 = request1;
                        }
                        if(request1 > checkBid && request1 > 0 && request < lot && request > 0 && balance1 > request){
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
                    pfc.click(cancel);
                    pfc.sleep(requestDelay);
                }

                if(multiReqMode){
                    if(request1 < request2){
                        request2 = request1;
                    }
                    if(request1 > request2 && request1 > 0 && request < lot && request > 0 && balance1 > request){
                        request2 = request1;
                        makeRequest();
                        openWindow();
                        for(int i = 0; i <= multiReqCount - 1; i++){
                            readRequest();
                            requestCheck();
                            request2 = request1;
                            makeRequest();
                            openWindow();
                        }
                    }                
                }




                if(!apiMode && !multiReqMode){
                    if(request1 < request2){
                        request2 = request1;
                    }
                    if(request1 > request2 && request1 > 0 && request < lot && request > 0 && balance1 > request){
                        request2 = request1;
                        makeRequest();
                    }
                }
                pfc.sleep(baseDelay);
                readBalance();
                if(balance1 < balance2){
                    long runtime = (Time.getMillis() - startTime) / 1000;
                    float pur = balance2 - balance1;
                    float plus = (lot * 0.8f) - pur;
                    float type = (pur / lot) * 100;
                    String status;
                    if(type >= 50) {
                        status = crossEmoji + "ФУЛЛ";
                        full++;
                        buys++;
                    }
                    else {
                        status = checkEmoji + "ЛОУ";
                        low++;
                        buys++;
                    }    
                    String catchMsg =
                    "<tg-emoji emoji-id=\"5397782960512444700\">📌</tg-emoji><tg-emoji emoji-id=\"5343846262993088747\">🔠</tg-emoji><tg-emoji emoji-id=\"5341772876120877494\">🔠</tg-emoji><tg-emoji emoji-id=\"5341463084424789613\">🔠</tg-emoji><tg-emoji emoji-id=\"5343846262993088747\">🔠</tg-emoji><tg-emoji emoji-id=\"5343851043291690659\">🔠</tg-emoji><tg-emoji emoji-id=\"5397782960512444700\">📌</tg-emoji>\n" +
                    "━━━━━━━━━━━━━━━━━━━━━\n" +

                    "<blockquote>" +
                    "Тип улова: " + status + "\n" +
                    "</blockquote>" +

                    "<blockquote>" +
                    infoEmoji + "Данные об улове:\n" +
                    "Баланс: " + String.format("%.2f",balance1) + " G\n" +
                    "Цена покупки: " + String.format("%.2f",pur) + " G\n" +
                    "Профит: " + String.format("%.2f",plus) + " G\n" +
                    "</blockquote>" +

                    infoEmoji + "<b>инфо о скине:</b>" + "\n" +
                    "<blockquote expandable>" +
                    "Имя:" + pfc.getText(nameLeft,nameRight) + "\n" +
                    "</blockquote>" + 


                    infoEmoji + "<b>инфо о скрипте:</b>" + "\n" +
                    "<blockquote expandable>" +
                    "<tg-emoji emoji-id=\"5334544901428229844\">ℹ️</tg-emoji>" + "мод" + "\n" +
                    ActivePerebiv + "\n" +
                    "</blockquote>" + 

                    "<blockquote expandable>" +
                    "<tg-emoji emoji-id=\"5334544901428229844\">ℹ️</tg-emoji>" + "мод помощник перебива" + "\n" +
                    ActiveHelpPerebiv + "\n" +
                    "</blockquote>" + 

                    "<blockquote expandable>" +
                    "<tg-emoji emoji-id=\"5334544901428229844\">ℹ️</tg-emoji>" + "дополнительные моды" + "\n" +
                    activeModes + 
                    "</blockquote>"
                    ;   
                    pfc.click(sell);
                    pfc.sleep(350);
                    PhotoTg(catchMsg, "HTML"); 
                    pfc.sleep(350);  
                    pfc.click(outOfSale);            
                    balance2 = balance1;

                }
            }
        
        }
    }

}).start();







void utility(String comm){

    if(!telegram) return;
    if(!utility) return;

    if(comm == null || comm.isEmpty()) return;

    String[] parsComm = comm.split(" ");
    String cmd = parsComm[0];    
    String param = "";

    if(parsComm.length > 1) {
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < parsComm.length; i++) {
            if(sb.length() > 0) sb.append(" ");
            sb.append(parsComm[i]);
        }
        param = sb.toString();
    }




    if(cmd.equals("/help")){
        String help = 
        "<tg-emoji emoji-id=\"5436113877181941026\">❓</tg-emoji><tg-emoji emoji-id=\"5343851043291690659\">🔠</tg-emoji><tg-emoji emoji-id=\"5344019247095904772\">🔠</tg-emoji><tg-emoji emoji-id=\"5343599757640100781\">🔠</tg-emoji><tg-emoji emoji-id=\"5344075141800292672\">🔠</tg-emoji><tg-emoji emoji-id=\"5436113877181941026\">❓</tg-emoji>\n" +
        "━━━━━━━━━━━━━━━━━━━━━\n\n" +
        "<blockquote expandable>" +
        "/stats - статистика о скрипте\n\n" +
        "/screen - скриншот экрана\n" +
        "</blockquote>" +
        "<blockquote expandable>" +
        "/go - запустить скрипт\n" +
        "/stop - остоновить скрипт\n" +
        "</blockquote>" + 
        "<blockquote expandable>" +
        "/mode1 [номер мода] - сменить режим перебива\n[список модов /mode1]\n\n" +
        "/mode2 [номер мода] - сменить мод помощник перебива\n[список модов помощиников перебива /mode2]\n\n" +
        "/mode3 [номер мода] [on/off] - включить дополнительный мод" +
        "</blockquote>"
        ;
        sendTg(help, "HTML");
    }



    if(cmd.equals("/stats")){
        long runtime = (Time.getMillis() - startTime) / 1000;
        String statsMsg =
        "<tg-emoji emoji-id=\"5244837092042750681\">📈</tg-emoji><tg-emoji emoji-id=\"5343988572439474243\">🔠</tg-emoji><tg-emoji emoji-id=\"5341463084424789613\">🔠</tg-emoji><tg-emoji emoji-id=\"5341772876120877494\">🔠</tg-emoji><tg-emoji emoji-id=\"5341463084424789613\">🔠</tg-emoji><tg-emoji emoji-id=\"5343988572439474243\">🔠</tg-emoji><tg-emoji emoji-id=\"5246762912428603768\">📉</tg-emoji>\n" +
        "━━━━━━━━━━━━━━━━━━━━━\n\n" +

        "<b>Уловы:</b>\n" +
        "<blockquote expandable>" +
        "всего: " + buys + "\n" +
        "low: " + low + "\n" +
        "full: " + full + "\n" +
        "</blockquote>" +

        "<b>о скрипте:</b>\n" +
        "<blockquote expandable>" +
        "время работы: " + (runtime/3600) + "ч " + ((runtime%3600)/60) + "м " + (runtime%60) + "с\n" +
        "время: " + Time.getTime() + "\n" +
        "активный режим: " + ActivePerebiv + "\n" +
        "</blockquote>"
        ;
        sendTg(statsMsg, "HTML");
    }

    if(cmd.equals("/screen")){
        PhotoTg("<b>Скриншот экрана</b>", "HTML");
    }

    if(cmd.equals("/go")){
        if(activeScript){
            sendTg("<tg-emoji emoji-id=\"5416081784641168838\">🟢</tg-emoji>скрипт уже запущен", "HTML");
        }
        if(!activeScript){
            activeScript = true;
            check = Time.getMillis();
            sendTg("<tg-emoji emoji-id=\"5416081784641168838\">🟢</tg-emoji>скрипт запущен", "HTML");
        }
    }
    if(cmd.equals("/stop")){
        if(!activeScript){
            sendTg("<tg-emoji emoji-id=\"5411225014148014586\">🔴</tg-emoji>скрипт уже выключен", "HTML");
        }
        if(activeScript){
            activeScript = false;
            pfc.click(cross2);
            sendTg("<tg-emoji emoji-id=\"5411225014148014586\">🔴</tg-emoji>скрипт выключен", "HTML");
        }
    }
    



    if(cmd.equals("/mode1")){
        if(param.isEmpty()) {
            String mode1help =
            "<tg-emoji emoji-id=\"5436113877181941026\">❓</tg-emoji><tg-emoji emoji-id=\"5343992218866707114\">🔠</tg-emoji><tg-emoji emoji-id=\"5341371043275632507\">🔠</tg-emoji><tg-emoji emoji-id=\"5343716404656895141\">🔠</tg-emoji><tg-emoji emoji-id=\"5344019247095904772\">🔠</tg-emoji><tg-emoji emoji-id=\"5343824642127720038\">🔠</tg-emoji><tg-emoji emoji-id=\"5436113877181941026\">❓</tg-emoji>\n" +
            "━━━━━━━━━━━━━━━━━━━━━\n" +
            "1 - custom\n" +
            "2 - full\n" +
            "3 - half\n" +
            "4 - mirror\n" +
            "5 - roundv1\n" +
            "6 - roundv2\n" +
            "7 - randomv2\n" +
            "8 - random_custom\n" +
            "9 - random_full\n" +
            "10 - echo"
            ;
            sendTg(mode1help, "HTML");
        }
        else{
            try{
                int mode = Integer.parseInt(param);
                if(mode >= 1 && mode <= 10){
                    mainMode = mode;
                    ActivePerebiv = switcher?mods1[10]:mods1[mainMode - 1];
                    sendTg("мод изменён на:\n" + mainMode, "HTML");
                }
                else{
                    sendTg("мод должен быть от 1 до 10", "HTML");
                }
            }
            catch (Exception e){
                sendTg(dangerEmoji + "неверный синтаксис", "HTML");
            }
        }
    }

    if(cmd.equals("/mode2")){
        if(param.isEmpty()) {
            String mode2help =
            "<tg-emoji emoji-id=\"5436113877181941026\">❓</tg-emoji><tg-emoji emoji-id=\"5343992218866707114\">🔠</tg-emoji><tg-emoji emoji-id=\"5341371043275632507\">🔠</tg-emoji><tg-emoji emoji-id=\"5343716404656895141\">🔠</tg-emoji><tg-emoji emoji-id=\"5344019247095904772\">🔠</tg-emoji><tg-emoji emoji-id=\"5341307993155727079\">🔠</tg-emoji><tg-emoji emoji-id=\"5436113877181941026\">❓</tg-emoji>\n" +
            "━━━━━━━━━━━━━━━━━━━━━\n" +
            "1 - api\n" +
            "2 - miltireq\n" +
            "3 - none"
            ;
            sendTg(mode2help, "HTML");
        }
        else{
            try{
                int mode2 = Integer.parseInt(param);
                if(mode2 >= 1 && mode2 <= 3){
                    if(mode2 == 1){
                        if(apiMode){
                            sendTg("Мод помощник уже\n api", "HTML");
                        }
                        if(multiReqMode || (!multiReqMode && !apiMode)){
                            multiReqMode = false;
                            apiMode = true;
                            sendTg("Мод помощник изменён на\n api", "HTML");
                        }
                    }
                    if(mode2 == 2){
                        if(multiReqMode){
                            sendTg("Мод помощник уже\n multireq", "HTML");
                        }
                        if(apiMode || (!multiReqMode && !apiMode)){
                            apiMode = false;
                            multiReqMode = true;
                            sendTg("Мод помощник изменён на\n multireq", "HTML");
                        }
                    }

                    if(mode2 == 3){
                        if(!multiReqMode && !apiMode){
                            sendTg("Мод помощник уже\n none", "HTML");
                        }
                        if(apiMode || multiReqMode){
                            apiMode = false;
                            multiReqMode = false;
                            sendTg("Мод помощник изменён на\n none", "HTML");
                        }
                    }
                }
                else{
                    sendTg("мод должен быть от 1 до 3", "HTML");
                }
            }
            catch (Exception e){
                sendTg(dangerEmoji + "неверный синтаксис", "HTML");
            }
        }

    }

    if(cmd.equals("/mode3")){
        if(param.isEmpty()) {
            String mode3help =
            "<tg-emoji emoji-id=\"5436113877181941026\">❓</tg-emoji><tg-emoji emoji-id=\"5343992218866707114\">🔠</tg-emoji><tg-emoji emoji-id=\"5341371043275632507\">🔠</tg-emoji><tg-emoji emoji-id=\"5343716404656895141\">🔠</tg-emoji><tg-emoji emoji-id=\"5344019247095904772\">🔠</tg-emoji><tg-emoji emoji-id=\"5341280969221499158\">🔠</tg-emoji><tg-emoji emoji-id=\"5436113877181941026\">❓</tg-emoji>\n" +
            "━━━━━━━━━━━━━━━━━━━━━\n" +
            "1 - balguard\n" +
            "2 - chargeguard\n" +           
            "3 - buyguard\n" +
            "4 - antifull\n" +
            "5 - antifull2"
            ;
            sendTg(mode3help, "HTML");
        }
        else{
            try{
                String[] parts = param.split(" ");
                boolean newState = true;
                if(parts.length > 1 && parts[1].equals("off")) {
                    newState = false;
                }
                String stateText = newState ? (checkEmoji + "ВКЛЮЧЕН") : (crossEmoji + "ВЫКЛЮЧЕН");

                if(parts[0].equals("1")){
                    balanceGuard = newState;
                    sendTg("Мод: balanceGuard" + stateText, "HTML");
                }
                if(parts[0].equals("2")){
                    chargeGuard = newState;
                    sendTg("Мод: chargeGuard" + stateText, "HTML");
                }
                if(parts[0].equals("3")){
                    buyGuard = newState;
                    sendTg("Мод: buyGuard" + stateText, "HTML");
                }
                if(parts[0].equals("4")){
                    antiFull = newState;
                    sendTg("Мод: antiFull" + stateText, "HTML");
                }
                if(parts[0].equals("5")){
                    antiFull2 = newState;
                    sendTg("Мод: antiFull2" + stateText, "HTML");
                }
            }
            catch (Exception e){
                sendTg(dangerEmoji + "неверный синтаксис", "HTML");
            }
        }
    }

    if(cmd.equals("/kk")){
        checkKk = true;
    }

}

for(;;){
    String cmd = getCommand();
    if(cmd != null && !cmd.isEmpty()) {
        utility(cmd);
    }
    Thread.sleep(300);
}
