/**
 * OA 请假申请表 api 封装
 *
 * @Author:    lf
 * @Date:      2025-05-31 20:00:10
 * @Copyright  lf
 */
import { postRequest, getRequest } from '/@/lib/axios';

export const leaveApi = {

  /**
   * 分页查询  @author  lf
   */
  queryPage : (param) => {
    return postRequest('/leave/queryPage', param);
  },

  /**
   * 增加  @author  lf
   */
  add: (param) => {
      return postRequest('/leave/add', param);
  },

  /**
   * 修改  @author  lf
   */
  update: (param) => {
      return postRequest('/leave/update', param);
  },


  /**
   * 删除  @author  lf
   */
  delete: (id) => {
      return getRequest(`/leave/delete/${id}`);
  },

  /**
   * 批量删除  @author  lf
   */
  batchDelete: (idList) => {
      return postRequest('/leave/batchDelete', idList);
  },

    /**
     * 删除  @author  lf
     */
    getDetail: (id) => {
        return getRequest(`/leave/${id}`);
    },

};
