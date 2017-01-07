package com.miller.priceMargin.service;

import com.miller.priceMargin.model.moreCenterPriceMargin.SystemAllocation;

/**
 * Created by tonyqi on 17-1-6.
 */
public interface SystemAllocationService {

    void saveSystemAllocation(SystemAllocation systemAllocation);

    SystemAllocation getSystemAllocation();

}
