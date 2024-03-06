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
            :disabled="!enable"
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
    }
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
</style>
