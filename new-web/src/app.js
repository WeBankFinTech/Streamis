import { access, request as FRequest } from '@fesjs/fes';
import PageLoading from '@/components/PageLoading';
import {
    FTabs,
    FSwitch,
    FCheckbox,
    FModal,
    FSpin,
    FButton,
    FInput,
    FInputNumber,
    FForm,
    FTag,
    FSelect,
    FTable,
    FPagination,
    FDatePicker,
    FDropdown,
    FDrawer,
    FCheckboxGroup,
    FSelectTree,
    FSelectCascader,
    FTooltip,
    FSpace,
    FRadio,
    FRadioGroup,
    FEllipsis,
    FMessage,
} from '@fesjs/fes-design';
import './style/base.less';

// import store from '@/store';

export const beforeRender = {
    loading: <PageLoading />,
    action: async () => {
        let username = '';
        let roles = [];
        try {
            const res = await FRequest('api/v1/projector/role', {}, { method: 'GET' });
            username = res.username;
            roles = res.roles;
        } catch (error) {
            console.error(error);
        }

        if (Array.isArray(roles) && roles.length > 0) {
            access.setRole(roles[0].toLowerCase());
        }

        let role = 'noauth';
        sessionStorage.setItem('userLogin', true);
        // 如果没有设置模拟账户走原来正常初始化的逻辑
        const isSimulatorMode = !!sessionStorage.getItem('simulatedUser');
        if (!isSimulatorMode) {
            // 兜底处理
            if (roles.length && (roles.includes('admin') || roles.includes('ADMIN'))) {
                role = 'admin';
            }
            // 缓存原本的身份
            sessionStorage.setItem('firstRole', role);
            // 缓存原本身份的名字
            sessionStorage.setItem('firstUserName', username);
        }
        access.setRole(role);
        // 初始化应用的全局状态，可以通过 useModel('@@initialState') 获取，具体用法看@/components/UserCenter 文件
        return {
            userName: sessionStorage.getItem('firstUserName'),
        };
    },
};

export function onAppCreated({ app }) {
    app.use(FButton);
    app.use(FTabs);
    app.use(FSelect);
    app.use(FSwitch);
    app.use(FCheckbox);
    app.use(FCheckboxGroup);
    app.use(FModal);
    app.use(FInput);
    app.use(FInputNumber);
    app.use(FForm);
    app.use(FTag);
    app.use(FSpin);
    app.use(FTable);
    app.use(FPagination);
    app.use(FDatePicker);
    app.use(FDropdown);
    app.use(FDrawer);
    app.use(FSelectTree);
    app.use(FSelectCascader);
    app.use(FEllipsis);
    app.use(FSpace);
    app.use(FTooltip);
    app.use(FRadioGroup);
    app.use(FRadio);

    // 引入vuex
    // app.use(store);
}

export const request = {
    dataField: 'data',
    baseURL: BASEURL,
    withCredentials: false,
    timeout: 60000,
    responseDataAdaptor(data) {
        const result = Object.assign({}, data);
        // 5375用于判断script部分成功的情况
        // 200其他接口默认都使用这个
        result.code = ['200', 0].includes(result.status) ? '0' : result.status;
        return result;
    },
    errorHandler: {
        401({
            response: {
                data: {
                    data: { redirect },
                },
            },
        }) {
            const lastRedirect = sessionStorage.getItem('redirect_to_um_login');
            if (
                (!lastRedirect || +new Date() - lastRedirect > 3000)
                && redirect
            ) {
                sessionStorage.setItem('redirect_to_um_login', +new Date());
                const splitChar = redirect.indexOf('?') > 0 ? '&' : '?';
                const redirectUrl = `${redirect}${splitChar}link=${encodeURIComponent(
                    window.location.href,
                )}`;
                console.log(redirectUrl);
                window.location.href = redirectUrl;
            }
        },
        default(error) {
            console.warn(error, error.data?.message, error.response);
            // 200的时候error.data?.message
            // 非200的时候error.response?.data?.message
            // 重复请求err.msg
            if (error && error.msg === '重复请求') return;
            FMessage.error(error.data?.message || error.response?.data?.message || error.message || error.msg);
        },
    },
};
