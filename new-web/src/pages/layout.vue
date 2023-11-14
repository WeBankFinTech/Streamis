<template>
    <div class="wd-page" @click="changeMenusDisplay">
        <BHorizontalLayout v-if="!isEmbedInFrame" v-model:curPath="route.path" :menus="menus" @menuChange="onMenuClick">
            <template v-slot:top>
                <div class="wd-logo">
                    <div class="avatar ava">
                        <img class="ava" src="@/assets/images/icons/avatar.svg" />
                        <div class="user-menus-list ava" :class="{ active: showMenus }">
                            <div class="user-name ava">{{initialState.userName}}</div>
                            <div v-if="isAdminUser" class="wd-menu-item small ava" @click="showUserPanelFn"><img class="wd-menu-icon ava" src="@/assets/images/icons/simulatorUser.svg">{{$t('fesHeader.simulatedUser')}}<span v-if="selectedSUsername" class="s-user-ctn ava">({{selectedSUsername}}) <span class="user-logout-btn ava" @click.stop="exitSimulatorUser">{{$t('fesHeader.exit')}}</span></span></div>
                            <div class="wd-menu-item small ava" @click="languageSwitching"><img class="wd-menu-icon" src="@/assets/images/icons/language.svg">{{$t('fesHeader.language')}}</div>
                            <div class="wd-menu-item small ava" @click="logout"><img class="wd-menu-icon" src="@/assets/images/icons/esc.svg">{{$t('fesHeader.signOut')}}</div>
                        </div>
                        <span v-if="selectedSUsername" class="simulator-badge ava">{{$t('fesHeader.simulated')}}</span>
                    </div>
                </div>
                <FModal v-model:show="showUserPanel" :title="$t('fesHeader.select')" @ok="selectSimulatorUser" @cancel="showMenus = false">
                    <div class="simulator-user-list">
                        <div class="list-label">{{$t('fesHeader.simulatedUser')}}</div>
                        <div class="list-ctn">
                            <FSpin v-if="isLoadingUserData"></FSpin>
                            <FSelect v-else filterable :options="userData" @change="selectUser"></FSelect>
                        </div>
                    </div>
                </FModal>
            </template>
            <template v-slot:container>
                <router-view></router-view>
            </template>
        </BHorizontalLayout>
        <template v-else>
            <router-view></router-view>
        </template>
    </div>
</template>
<script setup>
import { ref, h, computed } from 'vue';
import {
    useModel, useI18n, locale, request as FRequest, useRouter, useRoute, access, createWatermark,
} from '@fesjs/fes';
import {
    FMessage, FModal, FMenu, FScrollbar,
} from '@fesjs/fes-design';
import { BHorizontalLayout } from '@fesjs/traction-widget';

const { t: $t } = useI18n();

const router = useRouter();
const route = useRoute();

// 被嵌入其他项目的情况下不显示侧边栏
// 如果url里面showMenus=true，即使被嵌入也要展示左侧菜单
// eslint-disable-next-line no-restricted-globals
const isEmbedInFrame = computed(() => top !== self && route.query.showMenus !== 'true');

const showUserPanel = ref(false);
const showMenus = ref(false);
function changeMenusDisplay(e) {
    const classlist = [...(e?.target?.classList || [])];
    showMenus.value = (classlist.includes('ava'));
}

const userData = ref([]);

const isLoadingUserData = ref(false);

// 确认是否为管理员用户
const isAdminUser = ref(sessionStorage.getItem('firstRole') === 'admin');


const menus = ref([{
    label: '审计日志',
    value: '/auditLog',
    icon: () => h(<fes-icon type="dashboard" />),
}, {
    label: '模板页面2',
    value: '/assets',
    icon: () => h(<fes-icon type="rules" />),
},
]);

// if (isAdminUser.value) {
//     // 系统设置只有管理员有权限
//     menus.value.push();
// }

const onMenuClick = (e) => {
    const path = e.value;
    if (/^https?:\/\//.test(path)) {
        window.open(path, '_blank');
    } else if (/^\//.test(path)) {
        router.push(path);
    } else {
        console.warn(
            '[plugin-layout]: 菜单的path只能使以http(s)开头的网址或者路由地址',
        );
    }
};

// 展示模拟用户选择面板
const showUserPanelFn = async () => {
    showUserPanel.value = true;

    try {
        if (userData.value.length === 0) {
            const { data } = await FRequest('api/v1/admin/user/base/all', {}, 'get');
            userData.value = data.map(item => ({
                value: item.username,
                label: item.username,
            }));
            console.log(userData.value);
            isLoadingUserData.value = false;
        }
    } catch (error) {
        isLoadingUserData.value = false;
    }
};

// 从缓存拿信息
const selectedSUsername = ref(sessionStorage.getItem('simulatedUser'));
let selectedSUsernameTemp = null;
const selectUser = (data) => {
    console.log(data);
    selectedSUsernameTemp = data;
};

const getUserRole = async (params) => {
    try {
        const { roles, username } = await FMessage('api/v1/projector/role', {}, { method: 'GET' });

        let role = 'noauth';
        if ((Array.isArray(roles) && roles && roles[0].toLowerCase().indexOf('admin') > -1) || role.toLowerCase().indexOf('admin') > -1) {
            role = 'admin';
        }
        access.setRole(role);
        if (!sessionStorage.getItem('simulatedUser')) {
            // access.set('FesUserName', username);
            sessionStorage.setItem('FesUserName', username);
        }
        router.push({ path: '/dashboard' });
    } catch (error) {
        const role = sessionStorage.getItem('firstRole');
        if (role) {
            access.setRole(role);
        } else {
            access.setRole('noauth');
        }
    }
};

// 选择虚拟账户
const selectSimulatorUser = async () => {
    if (!selectedSUsernameTemp) return FMessage.warn($t('fesHeader.selectUser'));
    selectedSUsername.value = selectedSUsernameTemp;
    await FRequest(`api/v1/admin/transfer_user/${selectedSUsernameTemp}`, {}, { method: 'GET' });
    sessionStorage.setItem('simulatedUser', selectedSUsernameTemp);
    getUserRole();
    showUserPanel.value = false;
    FMessage.success($t('toastSuccess.simulatedUser'));
    showMenus.value = false;
};
// 全局的初始化信息
const initialState = useModel('@@initialState');
console.log(initialState);
createWatermark({
    content: initialState.userName,
    timestamp: 'YYYY-MM-DD HH:mm',
});

// 普通退出
const logout = () => {
    sessionStorage.clear();
    window.location.href = `${BASEURL}/api/v1/logout`;
};

// 语言切换
const languageSwitching = () => {
    const language = localStorage.getItem('currentLanguage');
    if (language === 'zh-CN') {
        locale.setLocale({
            locale: 'en-US',
        });
        localStorage.setItem('currentLanguage', 'en-US');
    } else {
        locale.setLocale({
            locale: 'zh-CN',
        });
        localStorage.setItem('currentLanguage', 'zh-CN');
    }
    window.location.reload();
};

// 折叠或者展开菜单
const isSideBarCollapse = ref(false);
const toggleSideBar = () => {
    isSideBarCollapse.value = !isSideBarCollapse.value;
};

// 退出模拟账户
const exitSimulatorUser = () => {
    const role = sessionStorage.getItem('firstRole');
    FModal.confirm({
        title: `${$t('fesHeader.exitUser')}`,
        content: `${$t('message.user')}${selectedSUsername.value}`,
        onOk: async () => {
            try {
                await FRequest('api/v1/admin/transfer_user/exit', {}, { method: 'GET' });
                selectedSUsername.value = '';
                sessionStorage.removeItem('simulatedUser');
                getUserRole();
                access.setRole(role);
                FMessage.success($t('toastSuccess.simulatedOut'));
                showMenus.value = false;
                return Promise.resolve();
            } catch (err) {
                console.warn(err);
            }
        },
        onCancel() {
            showMenus.value = false;
            return Promise.resolve();
        },
    });
};
</script>
<style lang="less" scoped>
@import '@/style/varible.less';
:deep(.wd-horizontal-layout) {
    .wd-side-menus {
        .wd-logo {
            z-index: 20;
        }
    }
}
.wd-side-menus{
    .wd-logo{
        position: relative;
        background: url(@/assets/images/logo.svg) 16px center no-repeat;
        background-size: 110px;
        height: 64px;
        .simulator-badge{
            font-size: 12px;
            color: #B7B7BC;
        }
        .avatar{
            position: absolute;
            top: 4px;
            right: 0;
            z-index: 20;
            width: 56px;
            height: 40px;
            padding: 14px 16px 0;
            cursor: pointer;
            background: #fff;
            .user-menus-list{
                &.active {
                    display: block;
                }
                display: none;
                position: absolute;
                top: 40px;
                left: 16px;
                min-width: 160px;
                background: #fff;
                border-radius: 4px;
                box-shadow: 0 2px 12px rgba(15,18,34,.1);
                .user-name{
                    padding: 16px;
                    border-bottom: 1px solid rgba(15,18,34,0.06);
                }
            }
        }
    }
    .wd-menu-item {
        .s-user-ctn{
            position: relative;
            padding-right: 50px;
            .user-logout-btn{
                position: absolute;
                z-index: 10;
                top: 0;
                right: 0;
                padding: 0 8px;
                color: @blue-color;
            }
        }
    }
}
.simulator-user-list{
    display: flex;
    align-items: center;
    .list-label{
        width: 80px;
        padding-right: 16px;
    }
    .list-ctn{
        flex: 1;
    }
}
</style>
