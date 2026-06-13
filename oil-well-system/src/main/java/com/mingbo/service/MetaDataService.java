package com.mingbo.service;

import com.mingbo.pojo.GeneralRequestDTO;
import com.mingbo.pojo.PageVO;
import com.mingbo.pojo.ReferenceResource;
import org.springframework.dao.DataAccessException;

/**
 * 文件元数据业务类
 */
public interface MetaDataService {

    /**
     * 保存元数据
     * @param ref 元数据载体类
     */
    void saveMetaData(ReferenceResource ref);

    /**
     * 按页查询元数据
     * @param requestDTO 请求信息，包括管理员id，当前页和页项数
     * @return 页信息
     * @throws DataAccessException 数据库出错时抛出，如页号不合法
     */
    PageVO<ReferenceResource> getMetaDataPage(GeneralRequestDTO requestDTO) throws DataAccessException;
    String deleteMetaData(int id) throws DataAccessException;
}
