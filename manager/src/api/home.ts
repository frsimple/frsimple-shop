import request from '@/utils/request';

//查询字典项
export function homeTop() {
  return request({
    url: '/shop/dataDic/homeTop',
    method: 'get',
  });
}
