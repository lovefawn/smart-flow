import SmartLayout from '/@/layout/index.vue';
export const flowRouters = [
    {
        path: '/flow/flow-design',
        component: SmartLayout,
        hidden: true,
        permissions: ['flow:definition:design'],
        children: [
            {
                path: 'index/:id(\\d+)',
                component: () => import('/@/views/flow/definition/warm-flow.vue'),
                name: 'Design',
                meta: { title: '流程设计', activeMenu: '/flow/definition' }
            }
        ]
    },
    ]