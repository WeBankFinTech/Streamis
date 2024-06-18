<template>
  <div>
    <div
      class="itemWrap"
      v-if="!isSql"
    >
      <p>{{ $t('message.streamis.jobDetail.flinkJarPac') }}</p>
      <div>
        <Table
          :columns="columns"
          :data="jarData.mainClassJar || []"
          border
        >
          <template
            slot-scope="{ row }"
            slot="materialType"
          >
            {{ row.materialType === 'project' ? '项目资源' : 'Job资源' }}
          </template>
          <template
            slot-scope="{ row }"
            slot="operation"
          >
            <div>
              <a
                :href="`/api/rest_j/v1/streamis/streamProjectManager/project/files/download?id=${row.id}&projectName=${jarData.projectName || ''}&materialType=${row.materialType}`"
                download
              >
                <Button
                  type="primary"
                  style="width:55px;height:22px;background:rgba(22, 155, 213, 1);margin-right: 5px"
                >
                  {{ $t('message.streamis.projectFile.download') }}
                </Button>
              </a>
            </div>
          </template>
        </Table>
      </div>
    </div>
    <div
      class="itemWrap"
      v-if="isSql"
    >
      <p>{{ $t('message.streamis.jobDetail.sqlContent') }}</p>
      <div class="sql">{{ jarData.sql }}</div>
    </div>
    <div
      class="itemWrap"
      v-if="!isSql"
    >
      <p class="args-header">
        Program Arguement
        <span>
          <Button
            v-if="!editProgramArguement"
            type="primary"
            class="btn"
            :disabled="!enable || !jarData.editable"
            @click="toggleEditProgramArguement(true)"
          >编辑</Button>
          <Button
            v-if="editProgramArguement"
            type="primary"
            @click="handleEditProgramArguement()"
            class="btn"
            style="margin-right: 8px;"
          >确认</Button>
          <Button
            v-if="editProgramArguement"
            type="primary"
            class="btn"
            @click="toggleEditProgramArguement(false)"
          >取消</Button>
        </span>
      </p>
      <!-- <div class="programArguement">{{ jarData.args }}</div> -->
      <Input
        :disabled="!editProgramArguement"
        class="programArguementaaa"
        type="textarea"
        v-model="args"
      />
    </div>
    <div
      class="itemWrap"
      v-if="!isSql"
    >
      <p>{{ $t('message.streamis.jobDetail.dependJarPac') }}</p>
      <div>
        <Table
          :columns="columns.filter(item => item.key !== 'mainClass')"
          :data="jarData.dependencyJars || []"
          border
        >
          <template
            slot-scope="{ row }"
            slot="materialType"
          >
            {{ row.materialType === 'project' ? '项目资源' : 'Job资源' }}
          </template>
          <template
            slot-scope="{ row }"
            slot="operation"
          >
            <div>
              <a
                :href="`/api/rest_j/v1/streamis/streamProjectManager/project/files/download?id=${row.id}&projectName=${jarData.projectName || ''}&materialType=${row.materialType}`"
                download
              >
                <Button
                  type="primary"
                  style="width:55px;height:22px;background:rgba(22, 155, 213, 1);margin-right: 5px"
                >
                  {{ $t('message.streamis.projectFile.download') }}
                </Button>
              </a>
            </div>
          </template>
        </Table>
      </div>
    </div>
    <div
      class="itemWrap"
      v-if="!isSql"
    >
      <p>{{ $t('message.streamis.jobDetail.userResource') }}</p>
      <div>
        <Table
          :columns="columns.filter(item => item.key !== 'mainClass')"
          :data="jarData.resources || []"
          border
        >
          <template
            slot-scope="{ row }"
            slot="materialType"
          >
            {{ row.materialType === 'project' ? '项目资源' : 'Job资源' }}
          </template>
          <template
            slot-scope="{ row }"
            slot="operation"
          >
            <div>
              <a
                :href="`/api/rest_j/v1/streamis/streamProjectManager/project/files/download?id=${row.id}&projectName=${jarData.projectName || ''}&materialType=${row.materialType}`"
                download
              >
                <Button
                  type="primary"
                  style="width:55px;height:22px;background:rgba(22, 155, 213, 1);margin-right: 5px"
                >
                  {{ $t('message.streamis.projectFile.download') }}
                </Button>
              </a>
            </div>
          </template>
        </Table>
      </div>
    </div>
    <div
      class="itemWrap"
      v-if="!isSql"
    >
      <p class="args-header">
        项目模板文件
        <span v-if="jarData.jobTemplate">
          <Button
            v-if="!editProgramArguement"
            type="primary"
            class="btn"
            @click="showTemplate"
            style="margin-right: 8px;"
          >查看</Button>
          <Button
            type="primary"
            class="btn"
            @click="downloadMetaJson"
          >
            {{ $t('message.streamis.projectFile.download') }}
          </Button>
        </span>
      </p>
      <div v-if="!jarData.jobTemplate" style="text-align: center;">暂无数据</div>
    </div>
    <Modal
      title="项目模板文件"
      v-model="templateVisible"
      footer-hide
      width="1200"
      @on-cancel="modalCancel"
      :class="full ? 'full' : ''"
    >
      <div>
        <div class="meta-wrapper">
          <Input
            ref="metaInputRef"
            v-model="meta"
            type="textarea"
            :autosize="{ minRows: 10, maxRows: 15 }"
            readonly
          />
          <span 
            class="full-btn"
            @click="fullToggle"
          >
            {{ full ? '> <' : '< >' }}
          </span>
        </div>
        <div class="btnWrap">
          <Button
            type="primary"
            @click="modalCancel"
            style="margin-left: 30px;"
          >
            {{ $t('message.streamis.jobHistoryColumns.close') }}
          </Button>
        </div>
      </div>
    </Modal>
  </div>
</template>
<script>
import api from '@/common/service/api'
export default {
  props: {
    jarData: {
      type: Object,
      default: () => ({})
    },
    isSql: Boolean
  },
  data() {
    return {
      columns: [
        {
          title: 'id',
          key: 'id'
        },
        {
          title: this.$t('message.streamis.jobDetail.columns.type'),
          key: 'materialType',
          slot: 'materialType',
        },
        {
          title: this.$t('message.streamis.jobDetail.columns.name'),
          key: 'fileName'
        },
        {
          title: this.$t('message.streamis.jobDetail.columns.version'),
          key: 'version'
        },
        {
          title: this.$t(
            'message.streamis.jobDetail.columns.versionDescription'
          ),
          key: 'comment'
        },
        {
          title: "Main Class",
          key: "mainClass"
        },
        {
          title: this.$t(
            'message.streamis.jobDetail.columns.versionUploadTime'
          ),
          key: 'createTime'
        },
        {
          title: this.$t('message.streamis.jobDetail.columns.operation'),
          key: 'operation',
          slot: 'operation'
        }
      ],
      projectName: this.$route.params.projectName,
      editProgramArguement: false,
      args: JSON.stringify(this.jarData.args),
      argsBak: JSON.stringify(this.jarData.args),
      version: '',
      enable: this.$route.params.enable, // 任务是否启用
      templateVisible: false,
      meta: '',
      full: false,
    }
  },
  methods: {
    toggleEditProgramArguement(value) {
      this.editProgramArguement = value
      if (!value) {
        // 取消编辑
        this.args = this.argsBak
      }
    },
    async handleEditProgramArguement() {
      try {
        // 确认编辑
        console.log('handleEditProgramArguement')
        const { id, version } = this.$route.params
        console.log(JSON.parse(this.args), id, version)
        if (this.args.length > 100000) {
          this.$Message.error('Program Arguement长度不能超过100000')
          return
        }
        if (!Array.isArray(JSON.parse(this.args))) {
          this.$Message.error('请输入正确的Program Arguement')
          return
        }
        await api.fetch('/streamis/streamJobManager/job/updateContent', {
          jobId: id,
          version: this.version || version,
          args: JSON.parse(this.args)
        }, 'post')
        const details = await api.fetch('/streamis/streamJobManager/job/jobInfo', { jobId: id, }, 'get')
        this.version = details.jobInfo.currentVersion || ''
        this.editProgramArguement = false
        this.argsBak = this.args
      } catch (error) {
        console.log(error)
        if (error instanceof SyntaxError) {
          this.$Message.error('请输入正确的Program Arguement')
        }
      }
    },
    formatJSON(text) {
      try {
        const jsonObj = JSON.parse(text)
        const formatJSON = JSON.stringify(jsonObj, null, 2)
        return formatJSON
      } catch (error) {
        return text
      }
    },
    fullToggle() {
      this.full = !this.full
    },
    modalCancel() {
      this.templateVisible = false
    },
    showTemplate(){
      this.templateVisible = true
      this.meta = this.formatJSON(this.jarData.jobTemplate ? this.jarData.jobTemplate.metaJson : '')
    },
    downloadMetaJson() {
      if (!this.meta) {
        this.$Message.error({ content: '模版为空' })
        return
      }
      const blob = new Blob([this.meta], { type: 'application/json' })
      const url = URL.createObjectURL(blob);

      const a = document.createElement('a');
      a.href = url;
      a.download = `${this.projectName}.template.json`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);

      // 下载完成后移除 <a> 标签
      document.body.removeChild(a);
    },
  },
  watch: {
    jarData: {
      deep: true,
      handler() {
        this.args = JSON.stringify(this.jarData.args)
        this.argsBak = JSON.stringify(this.jarData.args)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.itemWrap {
  padding: 10px;

  &>p {
    font-weight: 700;
    font-size: 16px;
  }

  &>div {
    padding-left: 20px;
    padding-top: 10px;
  }
}

.args-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .btn {
    height: 22px;
  }
}

.programArguement {
  background: rgba(94, 94, 94, 1);
  color: #fff;
  padding: 10px 20px;

}

.programArguementaaa {
  min-height: 64px;
}

.sql {
  background: #f8f8f9;
  padding: 10px 20px;
  min-height: 64px;
}

.meta-wrapper{
  position: relative;
  .full-btn{
    position: absolute;
    cursor: pointer;
    top: 5px;
    right: 8px;
    font-weight: bold;
  }
}
.full {
  /deep/.ivu-modal{
    width: 100vw !important;
    height: 100vh;
    min-height: 430px;
    top:0;
    .ivu-modal-content{
      height: 100%;
    }
  }
  /deep/textarea{
   height: calc(100vh - 200px) !important;
   min-height: 200px;
   max-height: calc(100vh - 200px) !important;
  }
}
.btnWrap {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}
</style>
