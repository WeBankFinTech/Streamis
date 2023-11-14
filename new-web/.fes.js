// .fes.js 只负责管理编译时配置，只能使用plain Object
import path from 'path';

export default {
    publicPath: './',
    access: {
        roles: {
            noauth: ["*"],
            admin: ["*"]
        }
    },
    monacoEditor: {
        languages: ['log']
    },
    layout: false,
    define: {
        BASEURL: 'http://10.107.97.166:8088/api/rest_j/v1/'
    },
    alias: {
        '@': path.resolve(__dirname, 'src')
    },
    devServer: {
        port: 8100
    },
    locale: {
        legacy: false
    },
    extraBabelPlugins:[
        [
            "import",
            {
                "libraryName": "@fesjs/fes-design",
                camel2DashComponentName: false,
                "customName": (name) => {
                    name = name.slice(1).replace(/([A-Z])/g, "-$1").toLowerCase().slice(1)
                    return `@fesjs/fes-design/es/${name}`;
                },
                "style": (name) => {
                    return `${name}/style`;
                }
            },
            'fes-design'
        ]
    ]
};
