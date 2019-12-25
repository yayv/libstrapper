import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.bcel.internal.generic.ATHROW;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 工具类
 * 对前端传过来的jsonObject进行解析，看是否后端给出的规则，按照一定规则返回正确的值
 */
public class CheckUtils {
    public static String[] typeArr = {"email", "phone", "mobile", "date", "time", "datetime", "int", "float", "String", "text", "base64", "MD5"};

    /**
     *  传入的字符串按规则处理
     * @param formart 规则字符串
     * @param valueString 参数字符串
     * @return 规则处理后的字符串
     */
    public static Object getValue(String formart, String valueString) {
        if(formart.contains("//")){
            formart=formart.substring(0,formart.indexOf("//"));
        }
        String type=null;
        for (String typestr : typeArr) {
            if (formart.contains(typestr)){
                type=typestr;
            }
        }
        if(!isValidRule(formart)){
            throw new RuntimeException("系统给出的规则不正确");
        }
        if("".equals(valueString.trim()) || valueString.equals(null) ){
            return findDefault(formart,valueString);
        }
    if(type.equals("String")){
            if(formart.contains(":")){
                        String longStr=null;
                        if(formart.indexOf("#")!=-1){
                            longStr=formart.substring(formart.indexOf(":")+1,formart.indexOf("#"));
                        }else {
                            longStr=formart.substring(formart.indexOf(":")+1);
                        }
                        int longNum=Integer.valueOf(longStr);
                        if(valueString.length()>longNum){
                            return findDefault(formart,valueString);
                        }
            }
            //枚举
            if (formart.contains("{") && formart.contains("}")){
                return judgeEnum(formart,valueString);
            }
            return valueString;
        }
        if(type.equals("int")){
            Pattern pattern=Pattern.compile("[0-9]*");
            if(pattern.matcher(valueString).matches()){
                if (formart.contains(":") && formart.contains("#")){
                    String longNumStr = formart.substring(formart.indexOf(":") + 1, formart.indexOf("#"));
                    int longNum=Integer.valueOf(longNumStr);
                    int length = valueString.length();
                    if(length>longNum){return findDefault(formart,valueString);}
                }
                if(formart.contains(":") && !formart.contains("#")){
                    String longNumStr = formart.substring(formart.indexOf(":") + 1, formart.length());
                    int longNum=Integer.valueOf(longNumStr);
                    int length = valueString.length();
                    if(length>longNum){return findDefault(formart,valueString);}
                }
                if (formart.contains("{") && formart.contains("}")){
                    return judgeEnum(formart,valueString);
                }
                if (formart.indexOf('(') != -1 && formart.indexOf(')') != -1) {
                    if(isIntdateRange(formart,valueString,'(',')')==false){
                        return findDefault(formart,valueString);
                    }
                }
                if (formart.indexOf('[') != -1 && formart.indexOf(']') != -1) {
                    if(isIntdateRange(formart,valueString,'[',']')==false){
                        return findDefault(formart,valueString);
                    }
                }
                if (formart.indexOf('(') != -1 && formart.indexOf(']') != -1) {
                    if(isIntdateRange(formart,valueString,'(',']')==false){
                        return findDefault(formart,valueString);
                    }
                }
                if (formart.indexOf('[') != -1 && formart.indexOf(')') != -1) {
                    if(isIntdateRange(formart,valueString,'[',')')==false){
                        return findDefault(formart,valueString);
                    }
                }
                return valueString;
            }else {
                return findDefault(formart,valueString);
            }
        }
        if (type.equals("mobile")){
            Pattern pattern=Pattern.compile("^((00\\d\\d-)|(\\+\\d\\d-))?1[3456789]\\d{9}$");
            if(pattern.matcher(valueString).matches()){
                return valueString;
            }else {
                return findDefault(formart,valueString);
            }
        }
        if (type.equals("phone")){
            Pattern pattern=Pattern.compile("^(\\d{3,4}-\\d{4,8})|((00\\d\\d-)|(\\+\\d\\d-))?1[3456789]\\d{9}$");
            if(pattern.matcher(valueString).matches()){
                if(formart.contains(":") && !formart.contains("#")){
                    String phoneLen=formart.substring(formart.indexOf(":") + 1, formart.length());
                    int longN=Integer.valueOf(phoneLen);
                    int length = valueString.length();
                    if(length>longN){return findDefault(formart,valueString);}
                }
                if(formart.contains(":") && formart.contains("#")){
                    String phoneLen=formart.substring(formart.indexOf(":") + 1, formart.indexOf("#"));
                    int longN=Integer.valueOf(phoneLen);
                    int length = valueString.length();
                    if(length>longN){return findDefault(formart,valueString);}
                }
                if(formart.contains("{") && formart.contains("}")){
                    return judgeEnum(formart,valueString);
                }
                return valueString;
            }else {
                return findDefault(formart,valueString);
            }
        }
        if(type.equals("date")){
            Pattern pattern=Pattern.compile("^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$");
            if(pattern.matcher(valueString).matches()){
            }else {
                return findDefault(formart,valueString);
            }
            if(formart.contains("{") && formart.contains("}")){
                return judgeEnum(formart,valueString);
            }
            if (formart.indexOf('(') != -1 && formart.indexOf(')') != -1) {
                if(isDateRange(formart,valueString,'(',')',"yyyy-MM-dd")==false){
                    return findDefault(formart,valueString);
                }
            }
            if (formart.indexOf('[') != -1 && formart.indexOf(']') != -1) {
                if(isDateRange(formart,valueString,'[',']',"yyyy-MM-dd")==false){
                    return findDefault(formart,valueString);
                }
            }
            if (formart.indexOf('(') != -1 && formart.indexOf(']') != -1) {
                if(isDateRange(formart,valueString,'(',']',"yyyy-MM-dd")==false){
                    return findDefault(formart,valueString);
                }
            }
            if (formart.indexOf('[') != -1 && formart.indexOf(')') != -1) {
                if(isDateRange(formart,valueString,'[',')',"yyyy-MM-dd")==false){
                    return findDefault(formart,valueString);
                }
            }
            return valueString;
        }
        if(type.equals("time")){
            Pattern pattern=Pattern.compile("^(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$");
            if(pattern.matcher(valueString).matches()){
                //判断枚举
                if(formart.contains("{") && formart.contains("}")){
                    return judgeEnum(formart,valueString);
                }
                //判断取值范围
                if (formart.indexOf('(') != -1 && formart.indexOf(')') != -1) {
                    if(isDateRange(formart,valueString,'(',')',"HH:mm:ss")==false){
                        return findDefault(formart,valueString);
                    }
                }
                if (formart.indexOf('[') != -1 && formart.indexOf(']') != -1) {
                    if(isDateRange(formart,valueString,'[',']',"HH:mm:ss")==false){
                        return findDefault(formart,valueString);
                    }
                }
                if (formart.indexOf('(') != -1 && formart.indexOf(']') != -1) {
                    if(isDateRange(formart,valueString,'(',']',"HH:mm:ss")==false){
                        return findDefault(formart,valueString);
                    }
                }
                if (formart.indexOf('[') != -1 && formart.indexOf(')') != -1) {
                    if(isDateRange(formart,valueString,'[',')',"HH:mm:ss")==false){
                        return findDefault(formart,valueString);
                    }
                }
                    return valueString;
            }else {
                return findDefault(formart,valueString);
            }
        }
        if(type.equals("datetime")){
            Pattern pattern=Pattern.compile("^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$");
            if(pattern.matcher(valueString).matches()){
                if(formart.contains("{") && formart.contains("}")){
                    return judgeEnum(formart,valueString);
                }
                //判断取值范围
                if (formart.indexOf('(') != -1 && formart.indexOf(')') != -1) {
                    if(isDateRange(formart,valueString,'(',')',"yyyy-MM-dd HH:mm:ss")==false){
                        return findDefault(formart,valueString);
                    }
                }
                if (formart.indexOf('[') != -1 && formart.indexOf(']') != -1) {
                    if(isDateRange(formart,valueString,'[',']',"yyyy-MM-dd HH:mm:ss")==false){
                        return findDefault(formart,valueString);
                    }
                }
                if (formart.indexOf('(') != -1 && formart.indexOf(']') != -1) {
                    if(isDateRange(formart,valueString,'(',']',"yyyy-MM-dd HH:mm:ss")==false){
                        return findDefault(formart,valueString);
                    }
                }
                if (formart.indexOf('[') != -1 && formart.indexOf(')') != -1) {
                    if(isDateRange(formart,valueString,'[',')',"yyyy-MM-dd HH:mm:ss")==false){
                        return findDefault(formart,valueString);
                    }
                }
                return valueString;
            }else {
                return findDefault(formart,valueString);
            }
        }
        if(type.equals("float")){
            Pattern pattern=Pattern.compile("^\\d+\\.\\d+(f|F)$");
            if(pattern.matcher(valueString).matches()){
                if(formart.contains("{") && formart.contains("}")){
                    return judgeEnum(formart,valueString);
                }
                if (formart.indexOf('(') != -1 && formart.indexOf(')') != -1) {
                    if(isFloatdateRange(formart,valueString,'(',')')==false){
                        return findDefault(formart,valueString);
                    }
                }
                if (formart.indexOf('[') != -1 && formart.indexOf(']') != -1) {
                    if(isFloatdateRange(formart,valueString,'[',']')==false){
                        return findDefault(formart,valueString);
                    }
                }
                if (formart.indexOf('(') != -1 && formart.indexOf(']') != -1) {
                    if(isFloatdateRange(formart,valueString,'(',']')==false){
                        return findDefault(formart,valueString);
                    }
                }
                if (formart.indexOf('[') != -1 && formart.indexOf(')') != -1) {
                    if(isFloatdateRange(formart,valueString,'[',')')==false){
                        return findDefault(formart,valueString);
                    }
                }
                return valueString;
             }else {
                return findDefault(formart,valueString);
            }
        }
        if(type.equals("MD5")){
            Pattern pattern=Pattern.compile("^([a-zA-Z0-9]{16})|([a-zA-Z0-9]{32})$");
            if(pattern.matcher(valueString).matches()){
                return valueString;
            }else {
                return findDefault(formart,valueString);
            }
        }
        if(type.equals("email")){
            Pattern pattern=Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
            if(pattern.matcher(valueString).matches()){
                if(formart.contains("{") && formart.contains("}")){
                    return judgeEnum(formart,valueString);
                }
                return valueString;
            }else {
                return findDefault(formart,valueString);
            }
        }
        if(type.equals("base64")){
            Pattern pattern=Pattern.compile("^([a-z0-9]|\\+|\\/){0,}$");
            if(pattern.matcher(valueString).matches()){
                return valueString;
            }else {
                return findDefault(formart,valueString);
            }
        }
        if(type.equals("text")){
            Pattern pattern=Pattern.compile("^.*$");
            if(pattern.matcher(valueString).matches()){
                if(formart.contains("{") && formart.contains("}")){
                    return judgeEnum(formart,valueString);
                }
                if (formart.equals(":") &&formart.equals("#")){
                    String length=formart.substring(formart.indexOf(":")+1,formart.indexOf("#"));
                    int len=Integer.valueOf(length);
                    if(valueString.length()>len){
                        return findDefault(formart,valueString);
                    }
                }
                if (formart.equals(":") && !formart.equals("#")){
                    String length=formart.substring(formart.indexOf(":")+1,formart.length());
                    int len=Integer.valueOf(length);
                    if(valueString.length()>len){
                        return findDefault(formart,valueString);
                    }
                }
                return valueString;
            }else {
                return findDefault(formart,valueString);
            }
        }
        return "类型尚未开发";
    }

    /**
     * 当前面规格出错，判断是否有默认值
     * @param formart 规则字符串
     * @param valueString 参数字符串
     * @return
     */
    protected static Object findDefault (String formart, String valueString){
        if (formart.indexOf("#") != -1) {
                //有默认值
                    String defalut = formart.substring(formart.indexOf("#")+1);
                    return defalut;
        }else {//没有默认值
                    if (formart.indexOf("*") == -1) {//非必填
                        return null;
                    } else {
                        throw new RuntimeException("这是必填项,您的输入有误");
                    }
        }
    }

    /**
     * 判断系统给规则是否符合规范
     * @param ruleString 规则字符串
     * @return
     */
    public static boolean isValidRule(String ruleString){
        String type=null;
        for(String typestr:typeArr){
            if(ruleString.contains(typestr)){
                type=typestr;
            }
        }
        String regex=null;
        if(type.equals("int")){
            regex="^(\\*|\\!|)int((\\[|\\()\\d+\\,\\d+(\\]|\\))|\\{\\d+\\,{0,}\\d+\\}|)(\\:\\d+|)(\\#\\d+|)$";
        }
        if(type.equals("String")){
            regex="^(\\*|\\!|)String(\\{(.\\,){0,}.{0,}\\}|)(\\:\\d+|)(\\#.+|)$";
        }
        if(type.equals("mobile")){
            regex="^(\\*|\\!|)mobile((\\{((00\\d\\d-)|(\\+\\d\\d-))?(\\d{11}\\,){0,}((00\\d\\d-)|(\\+\\d\\d-))?\\d{11}\\})|)(\\:\\d+|)(\\#((00\\d\\d-)|(\\+\\d\\d-))?\\d{11}|)$";
        }
        if(type.equals("phone")) {
            regex = "^(\\*|\\!|)phone((\\{((((00\\d\\d-)|(\\+\\d\\d-))?\\d{11}|\\d{3,4}-\\d{4,8})\\,){0,}(((00\\d\\d-)|(\\+\\d\\d-))?\\d{11}|\\d{3,4}-\\d{4,8})\\})|)(\\:\\d+|)((\\#(((00\\d\\d-)|(\\+\\d\\d-))?\\d{11}|\\d{3,4}\\-\\d{4,8}))|)$";
        }
        if(type.equals("date")){
            regex = "^(\\*|\\!|)date((\\(|\\[)[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\,[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])(\\)|\\])|)(\\{([1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\,){0,}[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])\\}|)(\\#[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])|)$";
        }
        if(type.equals("time")){
            regex = "^(\\*|\\!|)time((\\(|\\[)(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d\\,(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d(\\)|\\])|)(\\{((20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d\\,){0,}(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d\\}|)((#(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d)|)$";
        }
        if(type.equals("datetime")){
            regex = "^(\\*|\\!|)datetime(((\\(|\\[)[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d\\,[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d(\\)|\\]))|)(\\{([1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d\\,){0,}[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d\\})?(\\#[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d|)$";
        }
        if(type.equals("float")){
            regex = "^(\\*|\\!|)float((\\(|\\[)\\d+\\.\\d+(f|F)\\,\\d+\\.\\d+(f|F)(\\)|\\]))?(\\{(\\d+\\.\\d+(f|F)\\,){0,}\\#\\d+\\.\\d+(f|F)\\})?((\\#\\d+\\.\\d+(f|F))|)$";
        }
        if(type.equals("text")){
            regex = "^(\\*|\\!|)text((\\{(.*\\,){0,}.*\\})|)((\\:\\d+)|)(\\#.*|)$";
        }
        //65个
        if(type.equals("base64")){
            regex ="^(\\*|\\!|)base64((\\#([a-z0-9]|\\+|\\/||\\=){0,})|)$";
        }
        if(type.equals("MD5")){
            regex ="^(\\*|\\!|)MD5((\\#([a-zA-Z0-9]{16})|([a-zA-Z0-9]{32}))|)$";
        }
        if(type.equals("email")){
            regex ="^(\\*|\\!|)email(\\{(\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*\\,){0,}\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*\\})?(\\#\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*)?$";
        }
        Pattern pattern=Pattern.compile(regex);
        if(pattern.matcher(ruleString).matches()){
            return true;
        }else {
            return false;
        }
    }
    //对含有枚举的进行处理
    protected   static Object judgeEnum(String formart, String valueString){
        int startIndex=formart.indexOf('{');
        int endIndex=formart.indexOf('}');
        String range=formart.substring(startIndex+1,endIndex);
        String[] stringsarr=range.split(",");
        int i=0;
        for(String str:stringsarr){
            if(str.equals(valueString)){i++;}
        }
        if(i==0){
            return findDefault(formart, valueString);
        }else {
            return valueString;
        }
    }
    /**
     *
     * @param ruleJson  传入规则的json
     * @param paramsJson  前端传入参数的json
     * @return 根据规则返回的json
     */
//    public static JSONObject Rule(JSONObject ruleJson, JSONObject paramsJson){
//        for(String key:paramsJson.keySet()){
//            if (ruleJson.get(key)!=null){
//                Object getValue = CheckUtils.getValue(ruleJson.get(key).toString(), paramsJson.get(key).toString());
//                paramsJson.put(key,getValue);
//            }
//        }
//        return paramsJson;
//    }

    /**
     * int类型的取值范围
     * @param formart 规则
     * @param valueString 前端参数
     * @param charleft 左边括号类型
     * @param charright 右边括号类型
     * @return 是否符合规范
     */
    protected static boolean isIntdateRange(String formart,String valueString,char charleft,char charright){
        int startIndex = formart.indexOf(charleft);
        int endIndex = formart.indexOf(charright);
        String range = formart.substring(startIndex + 1, endIndex);
        String[] arr = range.split(",");
        int start = Integer.valueOf(arr[0]);
        int end = Integer.valueOf(arr[1]);
        if(charleft=='(' && charright==')'){
            if (Integer.valueOf(valueString) > start && Integer.valueOf(valueString) < end){
                return true;
            }
        }
        if(charleft=='(' && charright==']'){
            if (Integer.valueOf(valueString) > start && Integer.valueOf(valueString) <= end){
                return true;
            }
        }
        if(charleft=='[' && charright==')'){
            if (Integer.valueOf(valueString) >= start && Integer.valueOf(valueString) < end){
                return true;
            }
        }
        if(charleft=='[' && charright==']'){
            if (Integer.valueOf(valueString) >= start && Integer.valueOf(valueString) <= end){
                return true;
            }
        }
        return false;
    }

    /**
     * float类型的取值范围
     * @param formart
     * @param valueString
     * @param charleft
     * @param charright
     * @return
     */
    protected static boolean isFloatdateRange(String formart,String valueString,char charleft,char charright){
        int startIndex = formart.indexOf(charleft);
        int endIndex = formart.indexOf(charright);
        String range = formart.substring(startIndex + 1, endIndex);
        String[] arr = range.split(",");
        float start = Float.valueOf(arr[0]);
        float end = Float.valueOf(arr[1]);
        if(charleft=='(' && charright==')'){
            if (Float.valueOf(valueString) > start && Float.valueOf(valueString) < end){
                return true;
            }
        }
        if(charleft=='(' && charright==']'){
            if (Float.valueOf(valueString) > start && Float.valueOf(valueString) <= end){
                return true;
            }
        }
        if(charleft=='[' && charright==')'){
            if (Float.valueOf(valueString) >= start && Float.valueOf(valueString) < end){
                return true;
            }
        }
        if(charleft=='[' && charright==']'){
            if (Float.valueOf(valueString) >= start && Float.valueOf(valueString) <= end){
                return true;
            }
        }
        return false;
    }
    /**
     *  时间日期的取值范围
     * @param formart 规则
     * @param valueString 前端参数
     * @param charleft 左边括号类型
     * @param charright 右边括号类型
     * @param pattern 时间日期格式
     * @return
     */
    protected static boolean isDateRange(String formart,String valueString,char charleft,char charright,String pattern){
        int startIndex = formart.indexOf(charleft);
        int endIndex = formart.indexOf(charright);
        String range = formart.substring(startIndex + 1, endIndex);
        String[] arr = range.split(",");
        SimpleDateFormat format=new SimpleDateFormat(pattern);
        ParsePosition pos=new ParsePosition(0);
        ParsePosition pos1=new ParsePosition(0);
        ParsePosition posParam=new ParsePosition(0);
        Date paramdate=format.parse(valueString,posParam);
        Date strtodate1=format.parse(arr[0],pos);
        Date strtodate2=format.parse(arr[1],pos1);
        if(strtodate1.getTime()>strtodate2.getTime()){
            throw new RuntimeException("系统规则错误");
        }
        if(charleft=='(' && charright==')'){
            if (paramdate.getTime()>strtodate1.getTime() && paramdate.getTime()<strtodate2.getTime()){
                return true;
            }
        }
        if(charleft=='(' && charright==']'){
            if (paramdate.getTime()>strtodate1.getTime() && paramdate.getTime()<=strtodate2.getTime()){
                return true;
            }
        }
        if(charleft=='[' && charright==')'){
            if (paramdate.getTime()>=strtodate1.getTime() && paramdate.getTime()<=strtodate2.getTime()){
                return true;
            }
        }
        if(charleft=='[' && charright==']'){
            if (paramdate.getTime()>=strtodate1.getTime() && paramdate.getTime()<=strtodate2.getTime()){
                return true;
            }
        }
        return false;
    }
}
