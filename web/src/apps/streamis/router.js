export default [
  {
    path: '/realTimeJobCenter',
    name: 'RealTimeJobCenter',
    meta: {
      title: 'Streamis',
      keepAlive: false, // 缓存导致页面有多个编辑器，广播事件会触发报错
      publicPage: true, // 权限公开
    },
    component: () =>
      import('./view/realTimeJobCenter/index.vue'),
  },
  {
    path: '/realTimeJobCenter/:id',
    name: 'JobDetail',
    meta: {
      title: 'Streamis',
      keepAlive: false, // 缓存导致页面有多个编辑器，广播事件会触发报错
      publicPage: true, // 权限公开
    },
    component: () =>
      import('./view/jobDetail/index.vue'),
  },
  {
    path: '/dataSource',
    name: 'DataSource',
    meta: {
      title: 'Streamis',
      keepAlive: false, // 缓存导致页面有多个编辑器，广播事件会触发报错
      publicPage: true, // 权限公开
    },
    component: () =>
      import('./view/dataSource/index.vue'),
  },
  {
    path: '/realDataSource',
    name: 'RealDataSource',
    meta: {
      title: 'Streamis',
      keepAlive: false, // 缓存导致页面有多个编辑器，广播事件会触发报错
      publicPage: true, // 权限公开
    },
    component: () =>
      import('./view/realDataSource/index.vue'),
  }
]
