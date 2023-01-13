import { sendRequest } from '../../utils/request';

/** 获取收货地址 */
export function fetchDeliveryAddress(id) {
  return sendRequest({
    url: '/shop/wechat/user/getRevAddress/' + id,
    method: 'get',
  });
}

/** 获取收货地址列表 */
export function fetchDeliveryAddressList() {
  return sendRequest({
    url: '/shop/wechat/user/queryRevAddress',
    method: 'get',
  });
}

/** 删除收货地址 */
export function delRevAddress(id) {
  return sendRequest({
    url: '/shop/wechat/user/delRevAddress/' + id,
    method: 'get',
  });
}

/** 新增收货地址 */
export function addRevAddress(data) {
  return sendRequest({
    url: '/shop/wechat/user/addRevAddress',
    method: 'post',
    data: data,
  });
}
/** 修改收货地址 */
export function editRevAddress(data) {
  return sendRequest({
    url: '/shop/wechat/user/editRevAddress',
    method: 'post',
    data: data,
  });
}

/** 查询省市区树形 */
export function regTree() {
  return sendRequest({
    url: '/shop/dataDic/regTree',
    method: 'get',
  });
}

/** 修改收货地址 */
export function queryDefaultRevAddress(id) {
  return sendRequest({
    url: '/shop/wechat/order/queryDefaultRevAddress',
    method: 'get',
    data: {
      id: id,
    },
  });
}
