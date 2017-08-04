package com.lsh.base.data.utils;

import java.math.BigDecimal;

/**
 * Created by wuhao on 2016/11/23.
 */
public class PackUtil {
    public static String PackUnit2Uom(BigDecimal packUit, String eachUnitName){
        if(eachUnitName.compareTo("EA") == 0){
            return String.format("H%02d", packUit.intValue());
        }else if (eachUnitName.compareTo("KG") == 0){
            return String.format("H%.4fK", packUit.floatValue());
        }else if (eachUnitName.compareTo("G") == 0){
            return String.format("H%.4G", packUit.floatValue());
        }else{
            return String.format("H%.4N", packUit.floatValue());
        }
    }

    public static BigDecimal Uom2PackUnit(String uom){
        byte[] bytes = uom.getBytes();
        if(uom.compareTo("EA") == 0){
            return new BigDecimal("1");
        }
        if(uom.compareTo("KG") == 0){
            return new BigDecimal("1");
        }
        if(uom.compareTo("G") == 0){
            return new BigDecimal("1");
        }
        if(bytes[0] == 'S'){
            return new BigDecimal(uom.substring(1, bytes.length-1));
        }
        if(bytes[bytes.length-1] == 'K' || bytes[bytes.length-1] == 'G' || bytes[bytes.length-1] == 'N'){
            return new BigDecimal(uom.substring(1, bytes.length-1)).multiply(new BigDecimal(10));
        }else{
            return new BigDecimal(uom.substring(1));
        }
    }

    public static String Uom2EachUnitName(String uom){
        byte[] bytes = uom.getBytes();
        if(bytes.length==0){
            return "UNKNOWN";
        }
        switch (bytes[bytes.length-1]){
            case 'K':
                return "KG";
            case 'G':
                return "G";
            case 'N':
                return "UNKNOWN";
            default:
                return "EA";
        }
    }

    public static BigDecimal EAQty2UomQty(BigDecimal qty, BigDecimal packUnit){
        return qty.divide(packUnit, 4, BigDecimal.ROUND_HALF_DOWN);
    }

    public static BigDecimal EAQty2UomQty(BigDecimal qty, String uom){
        BigDecimal packUint = PackUtil.Uom2PackUnit(uom);
        return PackUtil.EAQty2UomQty(qty, packUint);
    }

    public static BigDecimal UomQty2EAQty(BigDecimal uomQty, BigDecimal packUint){
        return uomQty.multiply(packUint);
    }

    public static BigDecimal UomQty2EAQty(BigDecimal uomQty, String uom){
        BigDecimal packUint = PackUtil.Uom2PackUnit(uom);
        return PackUtil.UomQty2EAQty(uomQty, packUint);
    }
}
