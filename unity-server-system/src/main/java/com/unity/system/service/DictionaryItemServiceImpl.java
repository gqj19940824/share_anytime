
package com.unity.system.service;

import com.unity.common.base.BaseServiceImpl;
import com.unity.system.dao.DictionaryItemDao;
import com.unity.system.entity.DictionaryItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * ClassName: DictionaryItemService
 * Function: TODO ADD FUNCTION
 * Reason: TODO ADD REASON(可选)
 * date: 2018-12-21 13:31:12
 * 
 * @author creator 
 * @version  
 * @since JDK 1.8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DictionaryItemServiceImpl extends BaseServiceImpl<DictionaryItemDao,DictionaryItem>{

     public int removeDictionaryItemById(Long id){
         DictionaryItem dictionaryItem=  this.baseMapper.selectById(id);
         String code = dictionaryItem.getGradationCode();
       return this.baseMapper.removeDictionaryItemBycode(code);

     }
}
