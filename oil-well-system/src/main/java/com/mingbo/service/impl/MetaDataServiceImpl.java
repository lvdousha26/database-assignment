package com.mingbo.service.impl;

import com.mingbo.mapper.StorageMapper;
import com.mingbo.pojo.GeneralRequestDTO;
import com.mingbo.pojo.PageVO;
import com.mingbo.pojo.ReferenceResource;
import com.mingbo.service.MetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MetaDataServiceImpl implements MetaDataService {

    @Autowired
    private StorageMapper storageMapper;

    @Transactional
    @Override
    public void saveMetaData(ReferenceResource ref) {
        storageMapper.saveMetaData(ref);
    }

    @Override
    public PageVO<ReferenceResource> getMetaDataPage(GeneralRequestDTO requestDTO)
            throws DataAccessException {
        int currentPage = requestDTO.getCurrentPage();
        int pageSize = requestDTO.getPageSize();
        int userId = requestDTO.getUserId();

        int begin = (currentPage - 1) * pageSize;

        List<ReferenceResource> rows =
                storageMapper.getMetaData(userId, begin, pageSize);

        //6. 查询总记录数
        Long totalCount = storageMapper.selectTotalCount();

        //7. 封装PageBean对象
        PageVO<ReferenceResource> pageBean = new PageVO<>();
        pageBean.setRows(rows);
        pageBean.setTotalCount(totalCount);
        return pageBean;
    }

    @Override
    public String deleteMetaData(int id) throws DataAccessException {
        List<ReferenceResource> referenceResources = storageMapper.selectMetaDataById(id);
        String url = referenceResources.get(0).getStoragePath();
        storageMapper.deleteMetaDataById(id);
        return url;
    }
}
