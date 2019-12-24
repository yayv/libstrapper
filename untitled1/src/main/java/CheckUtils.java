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
     * @param strRule 规则字符串
     * @param strParam 参数字符串
     * @return 规则处理后的字符串
     */
    public static Object checkrule(String strRule, String strParam) {
        if(strRule.contains("//")){
            strRule=strRule.substring(0,strRule.indexOf("//"));
        }
        String type=null;
        for (String typestr : typeArr) {
            if (strRule.contains(typestr)){
                type=typestr;
            }
        }
        if(!isRule(strRule,type)){
            throw new RuntimeException("系统给出的规则不正确");
        }
        if("".equals(strParam.trim()) || strParam.equals(null) ){
            return findDefault(strRule,strParam);
        }
    if(type.equals("String")){
            if(strRule.contains(":")){
                        String longStr=null;
                        if(strRule.indexOf("#")!=-1){
                            longStr=strRule.substring(strRule.indexOf(":")+1,strRule.indexOf("#"));
                        }else {
                            longStr=strRule.substring(strRule.indexOf(":")+1);
                        }
                        int longNum=Integer.valueOf(longStr);
                        if(strParam.length()>longNum){
                            return findDefault(strRule,strParam);
                        }
            }
            //枚举
            if (strRule.contains("{") && strRule.contains("}")){
                return judgeEnum(strRule,strParam);
            }
            return strParam;
        }
        if(type.equals("int")){
            Pattern pattern=Pattern.compile("[0-9]*");
            if(pattern.matcher(strParam).matches()){
                if (strRule.contains(":") && strRule.contains("#")){
                    String longNumStr = strRule.substring(strRule.indexOf(":") + 1, strRule.indexOf("#"));
                    int longNum=Integer.valueOf(longNumStr);
                    int length = strParam.length();
                    if(length>longNum){return findDefault(strRule,strParam);}
                }
                if(strRule.contains(":") && !strRule.contains("#")){
                    String longNumStr = strRule.substring(strRule.indexOf(":") + 1, strRule.length());
                    int longNum=Integer.valueOf(longNumStr);
                    int length = strParam.length();
                    if(length>longNum){return findDefault(strRule,strParam);}
                }
                if (strRule.contains("{") && strRule.contains("}")){
                    return judgeEnum(strRule,strParam);
                }
                if (strRule.indexOf('(') != -1 && strRule.indexOf(')') != -1) {
                    if(isIntdateRange(strRule,strParam,'(',')')==false){
                        return findDefault(strRule,strParam);
                    }
                }
                if (strRule.indexOf('[') != -1 && strRule.indexOf(']') != -1) {
                    if(isIntdateRange(strRule,strParam,'[',']')==false){
                        return findDefault(strRule,strParam);
                    }
                }
                if (strRule.indexOf('(') != -1 && strRule.indexOf(']') != -1) {
                    if(isIntdateRange(strRule,strParam,'(',']')==false){
                        return findDefault(strRule,strParam);
                    }
                }
                if (strRule.indexOf('[') != -1 && strRule.indexOf(')') != -1) {
                    if(isIntdateRange(strRule,strParam,'[',')')==false){
                        return findDefault(strRule,strParam);
                    }
                }
                return strParam;
            }else {
                return findDefault(strRule,strParam);
            }
        }
        if (type.equals("mobile")){
            Pattern pattern=Pattern.compile("^((00\\d\\d-)|(\\+\\d\\d-))?1[3456789]\\d{9}$");
            if(pattern.matcher(strParam).matches()){
                return strParam;
            }else {
                return findDefault(strRule,strParam);
            }
        }
        if (type.equals("phone")){
            Pattern pattern=Pattern.compile("^(\\d{3,4}-\\d{4,8})|((00\\d\\d-)|(\\+\\d\\d-))?1[3456789]\\d{9}$");
            if(pattern.matcher(strParam).matches()){
                if(strRule.contains(":") && !strRule.contains("#")){
                    String phoneLen=strRule.substring(strRule.indexOf(":") + 1, strRule.length());
                    int longN=Integer.valueOf(phoneLen);
                    int length = strParam.length();
                    if(length>longN){return findDefault(strRule,strParam);}
                }
                if(strRule.contains(":") && strRule.contains("#")){
                    String phoneLen=strRule.substring(strRule.indexOf(":") + 1, strRule.indexOf("#"));
                    int longN=Integer.valueOf(phoneLen);
                    int length = strParam.length();
                    if(length>longN){return findDefault(strRule,strParam);}
                }
                if(strRule.contains("{") && strRule.contains("}")){
                    return judgeEnum(strRule,strParam);
                }
                return strParam;
            }else {
                return findDefault(strRule,strParam);
            }
        }
        if(type.equals("date")){
            Pattern pattern=Pattern.compile("^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$");
            if(pattern.matcher(strParam).matches()){
            }else {
                return findDefault(strRule,strParam);
            }
            if(strRule.contains("{") && strRule.contains("}")){
                return judgeEnum(strRule,strParam);
            }
            if (strRule.indexOf('(') != -1 && strRule.indexOf(')') != -1) {
                if(isDateRange(strRule,strParam,'(',')',"yyyy-MM-dd")==false){
                    return findDefault(strRule,strParam);
                }
            }
            if (strRule.indexOf('[') != -1 && strRule.indexOf(']') != -1) {
                if(isDateRange(strRule,strParam,'[',']',"yyyy-MM-dd")==false){
                    return findDefault(strRule,strParam);
                }
            }
            if (strRule.indexOf('(') != -1 && strRule.indexOf(']') != -1) {
                if(isDateRange(strRule,strParam,'(',']',"yyyy-MM-dd")==false){
                    return findDefault(strRule,strParam);
                }
            }
            if (strRule.indexOf('[') != -1 && strRule.indexOf(')') != -1) {
                if(isDateRange(strRule,strParam,'[',')',"yyyy-MM-dd")==false){
                    return findDefault(strRule,strParam);
                }
            }
            return strParam;
        }
        if(type.equals("time")){
            Pattern pattern=Pattern.compile("^(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$");
            if(pattern.matcher(strParam).matches()){
                //判断枚举
                if(strRule.contains("{") && strRule.contains("}")){
                    return judgeEnum(strRule,strParam);
                }
                //判断取值范围
                if (strRule.indexOf('(') != -1 && strRule.indexOf(')') != -1) {
                    if(isDateRange(strRule,strParam,'(',')',"HH:mm:ss")==false){
                        return findDefault(strRule,strParam);
                    }
                }
                if (strRule.indexOf('[') != -1 && strRule.indexOf(']') != -1) {
                    if(isDateRange(strRule,strParam,'[',']',"HH:mm:ss")==false){
                        return findDefault(strRule,strParam);
                    }
                }
                if (strRule.indexOf('(') != -1 && strRule.indexOf(']') != -1) {
                    if(isDateRange(strRule,strParam,'(',']',"HH:mm:ss")==false){
                        return findDefault(strRule,strParam);
                    }
                }
                if (strRule.indexOf('[') != -1 && strRule.indexOf(')') != -1) {
                    if(isDateRange(strRule,strParam,'[',')',"HH:mm:ss")==false){
                        return findDefault(strRule,strParam);
                    }
                }
                    return strParam;
            }else {
                return findDefault(strRule,strParam);
            }
        }
        if(type.equals("datetime")){
            Pattern pattern=Pattern.compile("^[1-9]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d$");
            if(pattern.matcher(strParam).matches()){
                if(strRule.contains("{") && strRule.contains("}")){
                    return judgeEnum(strRule,strParam);
                }
                //判断取值范围
                if (strRule.indexOf('(') != -1 && strRule.indexOf(')') != -1) {
                    if(isDateRange(strRule,strParam,'(',')',"yyyy-MM-dd HH:mm:ss")==false){
                        return findDefault(strRule,strParam);
                    }
                }
                if (strRule.indexOf('[') != -1 && strRule.indexOf(']') != -1) {
                    if(isDateRange(strRule,strParam,'[',']',"yyyy-MM-dd HH:mm:ss")==false){
                        return findDefault(strRule,strParam);
                    }
                }
                if (strRule.indexOf('(') != -1 && strRule.indexOf(']') != -1) {
                    if(isDateRange(strRule,strParam,'(',']',"yyyy-MM-dd HH:mm:ss")==false){
                        return findDefault(strRule,strParam);
                    }
                }
                if (strRule.indexOf('[') != -1 && strRule.indexOf(')') != -1) {
                    if(isDateRange(strRule,strParam,'[',')',"yyyy-MM-dd HH:mm:ss")==false){
                        return findDefault(strRule,strParam);
                    }
                }
                return strParam;
            }else {
                return findDefault(strRule,strParam);
            }
        }
        if(type.equals("float")){
            Pattern pattern=Pattern.compile("^\\d+\\.\\d+(f|F)$");
            if(pattern.matcher(strParam).matches()){
                if(strRule.contains("{") && strRule.contains("}")){
                    return judgeEnum(strRule,strParam);
                }
                if (strRule.indexOf('(') != -1 && strRule.indexOf(')') != -1) {
                    if(isFloatdateRange(strRule,strParam,'(',')')==false){
                        return findDefault(strRule,strParam);
                    }
                }
                if (strRule.indexOf('[') != -1 && strRule.indexOf(']') != -1) {
                    if(isFloatdateRange(strRule,strParam,'[',']')==false){
                        return findDefault(strRule,strParam);
                    }
                }
                if (strRule.indexOf('(') != -1 && strRule.indexOf(']') != -1) {
                    if(isFloatdateRange(strRule,strParam,'(',']')==false){
                        return findDefault(strRule,strParam);
                    }
                }
                if (strRule.indexOf('[') != -1 && strRule.indexOf(')') != -1) {
                    if(isFloatdateRange(strRule,strParam,'[',')')==false){
                        return findDefault(strRule,strParam);
                    }
                }
                return strParam;
             }else {
                return findDefault(strRule,strParam);
            }
        }
        if(type.equals("MD5")){
            Pattern pattern=Pattern.compile("^([a-zA-Z0-9]{16})|([a-zA-Z0-9]{32})$");
            if(pattern.matcher(strParam).matches()){
                return strParam;
            }else {
                return findDefault(strRule,strParam);
            }
        }
        if(type.equals("email")){
            Pattern pattern=Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
            if(pattern.matcher(strParam).matches()){
                if(strRule.contains("{") && strRule.contains("}")){
                    return judgeEnum(strRule,strParam);
                }
                return strParam;
            }else {
                return findDefault(strRule,strParam);
            }
        }
        if(type.equals("base64")){
            Pattern pattern=Pattern.compile("^([a-z0-9]|\\+|\\/){0,}$");
            if(pattern.matcher(strParam).matches()){
                return strParam;
            }else {
                return findDefault(strRule,strParam);
            }
        }
        if(type.equals("text")){
            Pattern pattern=Pattern.compile("^.*$");
            if(pattern.matcher(strParam).matches()){
                if(strRule.contains("{") && strRule.contains("}")){
                    return judgeEnum(strRule,strParam);
                }
                if (strRule.equals(":") &&strRule.equals("#")){
                    String length=strRule.substring(strRule.indexOf(":")+1,strRule.indexOf("#"));
                    int len=Integer.valueOf(length);
                    if(strParam.length()>len){
                        return findDefault(strRule,strParam);
                    }
                }
                if (strRule.equals(":") && !strRule.equals("#")){
                    String length=strRule.substring(strRule.indexOf(":")+1,strRule.length());
                    int len=Integer.valueOf(length);
                    if(strParam.length()>len){
                        return findDefault(strRule,strParam);
                    }
                }
                return strParam;
            }else {
                return findDefault(strRule,strParam);
            }
        }
        return "类型尚未开发";
    }

    /**
     * 当前面规格出错，判断是否有默认值
     * @param strRule 规则字符串
     * @param strParam 参数字符串
     * @return
     */
    public static Object findDefault (String strRule, String strParam){
        if (strRule.indexOf("#") != -1) {
                //有默认值
                    String defalut = strRule.substring(strRule.indexOf("#")+1);
                    return defalut;
        }else {//没有默认值
                    if (strRule.indexOf("*") == -1) {//非必填
                        return null;
                    } else {
                        throw new RuntimeException("这是必填项,您的输入有误");
                    }
        }
    }

    /**
     * 判断系统给规则是否符合规范
     * @param strRule 规则字符串
     * @param type 规则的类型
     * @return
     */
    public static boolean isRule(String strRule,String type){
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
        if(pattern.matcher(strRule).matches()){
            return true;
        }else {
            return false;
        }
    }
    //对含有枚举的进行处理
    public static Object judgeEnum(String strRule, String strParam){
        int startIndex=strRule.indexOf('{');
        int endIndex=strRule.indexOf('}');
        String range=strRule.substring(startIndex+1,endIndex);
        String[] stringsarr=range.split(",");
        int i=0;
        for(String str:stringsarr){
            if(str.equals(strParam)){i++;}
        }
        if(i==0){
            return findDefault(strRule, strParam);
        }else {
            return strParam;
        }
    }
    /**
     *
     * @param ruleJson  传入规则的json
     * @param paramsJson  前端传入参数的json
     * @return 根据规则返回的json
     */
    public static JSONObject Rule(JSONObject ruleJson, JSONObject paramsJson){
        for(String key:paramsJson.keySet()){
            if (ruleJson.get(key)!=null){
                Object checkrule = CheckUtils.checkrule(ruleJson.get(key).toString(), paramsJson.get(key).toString());
                paramsJson.put(key,checkrule);
            }
        }
        return paramsJson;
    }

    /**
     * int类型的取值范围
     * @param strRule 规则
     * @param strParam 前端参数
     * @param charleft 左边括号类型
     * @param charright 右边括号类型
     * @return 是否符合规范
     */
    public static boolean isIntdateRange(String strRule,String strParam,char charleft,char charright){
        int startIndex = strRule.indexOf(charleft);
        int endIndex = strRule.indexOf(charright);
        String range = strRule.substring(startIndex + 1, endIndex);
        String[] arr = range.split(",");
        int start = Integer.valueOf(arr[0]);
        int end = Integer.valueOf(arr[1]);
        if(charleft=='(' && charright==')'){
            if (Integer.valueOf(strParam) > start && Integer.valueOf(strParam) < end){
                return true;
            }
        }
        if(charleft=='(' && charright==']'){
            if (Integer.valueOf(strParam) > start && Integer.valueOf(strParam) <= end){
                return true;
            }
        }
        if(charleft=='[' && charright==')'){
            if (Integer.valueOf(strParam) >= start && Integer.valueOf(strParam) < end){
                return true;
            }
        }
        if(charleft=='[' && charright==']'){
            if (Integer.valueOf(strParam) >= start && Integer.valueOf(strParam) <= end){
                return true;
            }
        }
        return false;
    }

    /**
     * float类型的取值范围
     * @param strRule
     * @param strParam
     * @param charleft
     * @param charright
     * @return
     */
    public static boolean isFloatdateRange(String strRule,String strParam,char charleft,char charright){
        int startIndex = strRule.indexOf(charleft);
        int endIndex = strRule.indexOf(charright);
        String range = strRule.substring(startIndex + 1, endIndex);
        String[] arr = range.split(",");
        float start = Float.valueOf(arr[0]);
        float end = Float.valueOf(arr[1]);
        if(charleft=='(' && charright==')'){
            if (Float.valueOf(strParam) > start && Float.valueOf(strParam) < end){
                return true;
            }
        }
        if(charleft=='(' && charright==']'){
            if (Float.valueOf(strParam) > start && Float.valueOf(strParam) <= end){
                return true;
            }
        }
        if(charleft=='[' && charright==')'){
            if (Float.valueOf(strParam) >= start && Float.valueOf(strParam) < end){
                return true;
            }
        }
        if(charleft=='[' && charright==']'){
            if (Float.valueOf(strParam) >= start && Float.valueOf(strParam) <= end){
                return true;
            }
        }
        return false;
    }
    /**
     *  时间日期的取值范围
     * @param strRule 规则
     * @param strParam 前端参数
     * @param charleft 左边括号类型
     * @param charright 右边括号类型
     * @param pattern 时间日期格式
     * @return
     */
    public static boolean isDateRange(String strRule,String strParam,char charleft,char charright,String pattern){
        int startIndex = strRule.indexOf(charleft);
        int endIndex = strRule.indexOf(charright);
        String range = strRule.substring(startIndex + 1, endIndex);
        String[] arr = range.split(",");
        SimpleDateFormat format=new SimpleDateFormat(pattern);
        ParsePosition pos=new ParsePosition(0);
        ParsePosition pos1=new ParsePosition(0);
        ParsePosition posParam=new ParsePosition(0);
        Date paramdate=format.parse(strParam,posParam);
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
