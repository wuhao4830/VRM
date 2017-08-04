package com.lsh.base.data;

import com.lsh.base.data.bean.Delivery;

import java.util.List;
import java.util.Map;

/**
 * Created by wuhao on 16/1/6.
 */
public interface DeliveryWriterDao {
    Map writeDelivery(Delivery delivery,Map errorMap) throws Exception;
}
