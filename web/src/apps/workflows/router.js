export default [
  {
    path: '/workflow',
    name: 'Workflow',
    meta: {
      title: 'My Workflow',
      publicPage: true,
      parent: 'Project',
    },
    component: () =>
      import('./view/workflow/index.vue'),
  },
  {
    path: '/process',
    name: 'Process',
    meta: {
      title: 'DataSphere Studio',
      publicPage: true,
    },
    component: () =>
      import('./view/process/index.vue'),
  }
]