import request from '@/utils/request';

//查询字典项
export function dicVals(code) {
  return request({
    url: '/center/dict/vals/' + code,
    method: 'get',
  });
}

//上传图片返回图片路径
export function commonUploadImg(data) {
  return request({
    url: '/center/oss/uploadImg',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
}

//查询地图树形
export function regTree() {
  return request({
    url: '/shop/dataDic/regTree',
    method: 'get',
  });
}
