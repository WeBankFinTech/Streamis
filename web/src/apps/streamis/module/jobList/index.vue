<template>
  <div>
    <Spin v-if="baseLoading" fix></Spin>
    <titleCard :title="$t('message.streamis.moduleName.jobList')">
      <div>
        <div>
          <Form v-if="isBatching" inline>
            <FormItem>
              <Button
                type="primary"
                :disabled="!selections.length"
                @click="clickBatchRestart(true)"
                style="width:80px;height:30px;background:rgba(22, 155, 213, 1);display: flex;align-items: center;justify-content: center;"
              >
                {{$t('message.streamis.formItems.snapshotRestart')}}
              </Button>
            </FormItem>
            <FormItem>
              <Button
                type="primary"
                :disabled="!selections.length"
                @click="clickBatchRestart(false)"
                style="width:80px;height:30px;background:rgba(22, 155, 213, 1);margin-left: 16px;display: flex;align-items: center;justify-content: center;"
              >
                {{$t('message.streamis.formItems.directRestart')}}
              </Button>
            </FormItem>
            <FormItem>
              <Button
                type="primary"
                :disabled="!selections.length"
                @click="clickBatchEditTags()"
                style="width:80px;height:30px;background:rgba(22, 155, 213, 1);margin-left: 16px;display: flex;align-items: center;justify-content: center;"
              >
                {{$t('message.streamis.formItems.editTags')}}
              </Button>
            </FormItem>
            <FormItem>
              <Button
                type="primary"
                :disabled="!selections.length"
                @click="clickBatchDisable"
                style="width:80px;height:30px;background:rgba(22, 155, 213, 1);margin-left: 16px;display: flex;align-items: center;justify-content: center;"
              >
                禁用
              </Button>
            </FormItem>
            <FormItem>
              <Button
                type="primary"
                @click="hideButtons"
                style="width:80px;height:30px;background:rgba(22, 155, 213, 1);margin-left: 16px;display: flex;align-items: center;justify-content: center;"
              >
                {{$t('message.streamis.formItems.cancel')}}
              </Button>
            </FormItem>
          </Form>
          <Form ref="queryForm" class="query-form" :model="query" :rules="queryRuleValidate" inline v-else @submit.native.prevent :label-width="80">
            <FormItem :label="$t('message.streamis.formItems.jobName')">
              <Input
                v-model="query.jobName"
                :placeholder="$t('message.streamis.formItems.jobName')"
              >
              </Input>
            </FormItem>
            <FormItem
              :label="$t('message.streamis.formItems.jobStatus')"
            >
              <Select v-model="query.jobStatus" class="select">
                <Option
                  v-for="(item, index) in jobStatus"
                  :value="item"
                  :key="index"
                >
                  {{ $t('message.streamis.jobStatus.' + item) }}
                </Option>
              </Select>
            </FormItem>
            <FormItem
              label="启用状态"
            >
              <Select v-model="query.enable" @on-change="onEnableStatusChange" class="select">
                <Option
                  v-for="item in enableStatus"
                  :value="item.value"
                  :key="item.value"
                >
                  {{ item.label }}
                </Option>
              </Select>
            </FormItem>
            <FormItem label="作业类型">
              <Input
                v-model="query.jobType"
                placeholder="作业类型"
              >
              </Input>
            </FormItem>
            <FormItem prop="label" :label="$t('message.streamis.formItems.tag')">
              <Input
                v-model="query.label"
                :placeholder="$t('message.streamis.formItems.tagPlaceHolder')"
              >
              </Input>
            </FormItem>
            <FormItem class="button-item">
              <Button
                type="primary"
                @click="handleQuery()"
                style="width:100px;height:30px;background:rgba(22, 155, 213, 1);"
              >
                {{ $t('message.streamis.formItems.queryBtn') }}
              </Button>
            </FormItem>

            <FormItem class="button-item">
              <Button
                type="primary"
                @click="resetQuery()"
                style="width:100px;height:30px;background:rgba(22, 155, 213, 1);"
              >
                重置
              </Button>
            </FormItem>

            <FormItem class="button-item">
              <Button
                type="primary"
                :disabled="!query.enable"
                @click="showButtons"
                style="width:100px;height:30px;background:rgba(22, 155, 213, 1);"
              >
                {{$t('message.streamis.formItems.batchAction')}}
              </Button>
            </FormItem>

            <FormItem class="button-item">
              <Button
                v-if="!query.enable"
                type="primary"
                :disabled="!selections.length"
                @click="clickBatchEnable"
                style="width:100px;height:30px;background:rgba(22, 155, 213, 1);"
              >
                启用
              </Button>
            </FormItem>
          </Form>
          <Table ref="list" :row-class-name="rowClassName" :columns="columns" :data="tableDatas" :loading="loading" @on-selection-change="selectionChange" :class="{ table: isBatching || (!query.enable) }">
            <template slot-scope="{ row, index }" slot="jobName">
              <div
                class="jobName"
                v-show="index === 0"
                @click="handleUpload()"
                v-if="false"
              >
                <Icon type="md-add" class="upload" />
                <span>{{
                  $t('message.streamis.jobListTableColumns.upload')
                }}</span>
              </div>
              <div class="jobName" v-show="index === 0">
                <Upload
                  :action="`/api/rest_j/v1/streamis/streamJobManager/job/upload?projectName=${projectName}`"
                  :on-success="jarUploadSuccess"
                  :on-error="jarUploadError"
                  :show-upload-list="false"
                >
                  <Icon type="md-add" class="upload" />
                  <span>{{
                    $t('message.streamis.jobListTableColumns.upload')
                  }}</span></Upload
                >
              </div>
              <div class="jobName" v-show="index !== 0">
                <Dropdown transfer transfer-class-name="joblist-table-ivu-select-dropdown" @on-click="name => handleRouter(row, name)">
                  <Icon type="md-more" class="more" />
                  <DropdownMenu slot="list">
                    <DropdownItem
                      v-for="(item, index) in jobMoudleRouter.concat(!row.enable ? 'enable' : 'disable')"
                      :disabled="!row.enable && item !== 'enable'"
                      :name="item"
                      :key="index"
                    >
                      {{ $t('message.streamis.jobMoudleRouter.' + item) }}
                    </DropdownItem>
                  </DropdownMenu>
                </Dropdown>
                <div style="margin-left: 5px" @click="handleRouter(row)">
                  <a href="javascript:void(0)">{{ row.name }} </a>
                </div>
              </div>
            </template>
            <template slot-scope="{ row, index }" slot="status">
              <div v-if="index !== 0">
                <strong :style="`color: ${row.statusObj.color || '#000'};`">
                  {{   row.statusObj.name ? $t('message.streamis.jobStatus.' + row.statusObj.name) : '' }}
                </strong>
                <span v-if="row.status === 6" class="failureReasonWrapper" @click="showFailureReason(row)">
                  {{ $t('message.streamis.jobStatus.failureReason') }}
                </span>
              </div>
            </template>
            <template slot-scope="{ row, index }" slot="enable">
              <div v-if="index !== 0">
                {{  row.enable ? '已启用' : '已禁用' }}
              </div>
            </template>
            <template slot-scope="{ row, index }" slot="version">
              <div v-show="index !== 0" class="versionWrap">
                <div class="version" @click="versionDetail(row)">
                  {{ row.version }}
                </div>
                <!-- 一般versionForwards字段大于零的情况，只发生在处于运行时状态的作业上，因为对运行时作业进行发布，作业的当前使用版本不会更新，所以已发布版本才会领先当前使用版本。 -->
                <div class="versionForwards">{{row.versionForwards > 0 ? '(+' + row.versionForwards + ')' : ''}}</div>
              </div>
            </template>
            <template slot-scope="{ row, index }" slot="operation">
              <div v-show="index !== 0">
                <Button
                  type="primary"
                  v-show="row.status !== 5"
                  :loading="row.buttonLoading"
                  style="height:22px;background:#008000;margin-right: 5px; font-size:10px;"
                  :disabled="!row.enable"
                  @click="handleAction(row, index)"
                >
                  {{ $t('message.streamis.formItems.startBtn') }}
                </Button>
                <Poptip placement="top" v-model="row.poptipVisible" :disabled="row.buttonLoading">
                  <Button
                    type="primary"
                    v-show="row.status === 5"
                    :disabled="row.buttonLoading"
                    :loading="row.buttonLoading"
                    style="height:22px;background:#ff0000;margin-right: 5px; font-size:10px;"
                  >
                    {{ $t('message.streamis.formItems.stopBtn') }}
                  </Button>
                  <div slot="content">
                    <div class="btn-wrap">
                      <Button
                        class="btn"
                        type="primary"
                        :disabled="row.buttonLoading"
                        style="height:22px;background:#ff0000;margin-right: 5px; font-size:10px;"
                        @click="handleStop(row, 0, index)"
                      >
                        {{ $t('message.streamis.formItems.directStop') }}
                      </Button>
                      <Button
                        class="btn"
                        type="primary"
                        :disabled="row.buttonLoading"
                        style="height:22px;background:#ff0000;margin-right: 5px; font-size:10px;"
                        @click="handleStop(row, 1, index)"
                      >
                        {{ $t('message.streamis.formItems.snapshotAndStop') }}
                      </Button>
                    </div>
                  </div>
                </Poptip>
                    
                <Button
                  type="primary"
                  @click="handleRouter(row, 'jobConfig')"
                  style="height:22px;background:rgba(22, 155, 213, 1);margin-right: 5px; font-size:10px;"
                >
                  {{ $t('message.streamis.formItems.configBtn') }}
                </Button>
        
              </div>
            </template>
          </Table>
          <Page
            :current="pageData.current"
            :total="pageData.total"
            class="page"
            :page-size="pageData.pageSize"
            show-total
            show-elevator
            show-sizer
            @on-change="handlePageChange"
            @on-page-size-change="handlePageSizeChange"
          />
        </div>
      </div>
    </titleCard>
    <versionDetail
      ref="versionDetail"
      :visible="modalVisible"
      :datas="versionDatas"
      :projectName="projectName"
      @modalCancel="modalCancel"
      @versionDetailPageChange="versionDetailPageChange"
    />
    <uploadJobJar
      :visible="uploadVisible"
      @jarModalCancel="jarModalCancel"
      @jarUploadSuccess="jarUploadSuccess"
    />
    <Modal
      v-model="processModalVisable"
      :title="modalTitle"
      width="800"
      footer-hide
      @on-visible-change="onClose"
    >
      <div class="wrap">
        <Spin v-if="modalLoading" fix></Spin>
        <div class="general">
          <div class="bar"></div>
          <div class="text">{{modalContent}}({{orderNum}}/{{selections.length}})</div>
        </div>
        <div class="info" v-if="snapPaths.length">
          <h4>{{$t('message.streamis.jobListTableColumns.snapshotInfo')}}:</h4>
          <div v-for="item in snapPaths" :key="item.taskId" style="margin-bottom: 32px;">
            <span>{{item.taskName}}</span>,
            <span style="margin-right: 32px;">{{item.taskId}}:</span>
            <span>{{item.info}}</span>
          </div>
        </div>
        <div class="info" v-if="failTasks.length">
          <h4>{{$t('message.streamis.jobListTableColumns.failInfo')}}:</h4>
          <div v-for="item in failTasks" :key="item.taskId" style="margin-bottom: 32px;">
            <span>{{item.taskName}}</span>,
            <span style="margin-right: 32px;">{{item.taskId}}:</span>
            <span>{{item.info}}</span>
          </div>
        </div>
        <div v-if="isFinish" style="text-align: center;">
          <Button
            type="primary"
            @click="onClose()"
          >
            {{ $t('message.streamis.formItems.confirmBtn') }}
          </Button>
        </div>
      </div>
    </Modal>
    <Modal
      v-model="snapModalVisable"
      :title="snapTitle"
      width="800"
      footer-hide
    >
      <div class="wrap">
        <div class="info">
          <h4>{{$t('message.streamis.jobListTableColumns.snapshotInfo')}}</h4>
          <div>
            {{snapshotPath}}
          </div>
        </div>
      </div>
    </Modal>
    <Modal
      v-model="startHintVisible"
      :title="$t('message.streamis.startHint.title')"
      width="1000"
      @on-cancel="cancelStartHint"
      :mask-closable="false"
    >
      <div class="wrap">
        <Table border :row-class-name="rowClassName" :columns="checkColumns" :data="checkData">
          <template slot-scope="{row}" slot="yarn">
            <div v-for="(item, index) in row.yarn" :key="index">
              <a class="red-color" v-if="item.applicationUrl && item.applicationUrl !== '无' && item.applicationId" style="display: block" @click="goToNewTab(item.applicationUrl)">{{item.yarnAppType}}: {{item.applicationName}}</a>
              <div v-else>{{ item.applicationUrl }}</div>
            </div>
          </template>
          <template #consistency="{row}">
            <div v-if="row.consistency.includes('不通过')" class="red-color">{{ row.consistency }}</div>
            <div v-else>{{ row.consistency }}</div>
          </template>
        </Table>
      </div>
      <template slot="footer">
        <div class="modal-footer-wrapper">
          <div class="modal-footer-descs">
            <div style="marginRight: 16px;">以上 {{checkData.length}} 个作业</div>
            <div v-if="hasWarningCount" class="warning-color">检查到 {{hasWarningCount}} 个作业存在异常，可能导致启动失败，请检查</div>
          </div>
          <div class="modal-footer-btns">
            <Button type="primary" @click="confirmStarting">{{ $t('message.streamis.formItems.confirmBtn') }}</Button>
            <Button type="primary" @click="cancelStartHint">{{ $t('message.streamis.formItems.cancel') }}</Button>
          </div>
        </div>
      </template>
    </Modal>
    <Modal
      v-model="showEditTags"
      :title="$t('message.streamis.formItems.editTags')"
      @on-visible-change="changeVisible">
      <Form ref="formValidate" :model="formValidate" :rules="ruleValidate" :label-width="0">
        <FormItem prop="newTag">
          <Input v-model="formValidate.newTag" :placeholder="$t('message.streamis.formItems.editTagsPlaceHolder')"></Input>
        </FormItem>
      </Form>
      <div>{{ $t('message.streamis.formItems.editTagsHint') }}</div>
      <template #footer>
        <Button @click="showEditTags = false">取消</Button>
        <Button type="primary" @click="confirmEditTags" :loading="editTagsLoading">确定</Button>
      </template>
    </Modal>
    <Modal
      v-model="failureReasonShow"
      :title="$t('message.streamis.formItems.failureReason')"
      :footer-hide="true"
    >
      <div class="text">
        {{ failureReason }}
      </div>
    </Modal>
  </div>
</template>
<script>
import api from '@/common/service/api'
import titleCard from '@/apps/streamis/components/titleCard'
import versionDetail from '@/apps/streamis/module/versionDetail'
import uploadJobJar from '@/apps/streamis/module/uploadJobJar'
import { allJobStatuses, enableStatus } from '@/apps/streamis/common/common'
import moment from 'moment'

/**
 * 渲染特殊表头
 */
function renderSpecialHeader(h, params) {
  return h('div', [
    h('strong', params.column.title),
    h(
      'span',
      {
        style: {
          color: 'red'
        }
      },
      '*'
    )
  ])
}

export default {
  components: { titleCard, versionDetail, uploadJobJar },
  data() {
    return {
      query: {
        jobName: '',
        jobStatus: 'all',
        jobType: '',
        label: '',
        enable: 1,
      },
      enableStatus: enableStatus,
      jobStatus: ['all'].concat(allJobStatuses.map(item => item.name)),

      // 批量修改标签
      showEditTags: false,
      editTagsLoading: false,
      newTag: '',
      formValidate: {
        newTag: ''
      },
      ruleValidate: {
        newTag: [
          { required: true, message: '请输入新标签', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_,.\s]+$/, message: '仅支持大小写字母、数字、下划线、小数点、逗号', trigger: 'blur' }
        ],
      },
      queryRuleValidate: {
        label: [
          { pattern: /^[a-zA-Z0-9_,.\s]+$/, message: '仅支持大小写字母、数字、下划线、小数点、逗号', trigger: 'blur' }
        ],
      },

      tableDatas: [
        {},
        // {
        //   id: 10,
        //   name: 'flinkJarTestc',
        //   workspaceName: null,
        //   projectName: 'flinkJarTest3',
        //   jobType: 'flink.jar',
        //   label: 'e,t,y,h,g',
        //   createBy: 'hdfs',
        //   createTime: 1636338541000,
        //   status: 0,
        //   version: 'v00002',
        //   lastVersionTime: 1636339360000,
        //   description: '这是一个FlinkJar测试Job3'
        // },
        // {
        //   id: 9,
        //   name: 'flinkSqlTesta',
        //   workspaceName: null,
        //   projectName: 'flinkSqlTestD',
        //   jobType: 'flink.sql',
        //   label: 'a,b,c',
        //   createBy: 'hdfs',
        //   createTime: 1636338055000,
        //   status: 0,
        //   version: 'v00001',
        //   lastVersionTime: 1636338055000,
        //   description: '这是一个FlinkSql测试JobD'
        // }
      ],
      baseLoading: false,
      modalLoading: false,
      failTasks: [],
      snapPaths: [],
      timer: null,
      isBatching: false,
      selections: [],
      processModalVisable: false,
      isFinish: false,
      modalTitle: '',
      snapTitle: '',
      snapshotPath: '',
      snapModalVisable: false,
      modalContent: '',
      orderNum: 0,
      columns: [
        {
          title: this.$t('message.streamis.jobListTableColumns.jobName'),
          key: 'name',
          renderHeader: renderSpecialHeader,
          slot: 'jobName'
        },
        {
          title: this.$t('message.streamis.jobListTableColumns.taskStatus'),
          key: 'status',
          renderHeader: renderSpecialHeader,
          slot: 'status'
        },
        {
          title: '启用状态',
          key: 'enable',
          renderHeader: renderSpecialHeader,
          slot: 'enable'
        },
        {
          title: this.$t('message.streamis.jobListTableColumns.jobType'),
          key: 'jobType',
          renderHeader: renderSpecialHeader
        },
        {
          title: this.$t('message.streamis.jobListTableColumns.manageMode'),
          key: 'manageModeChinese',
          renderHeader: renderSpecialHeader
        },
        {
          title: this.$t('message.streamis.jobListTableColumns.label'),
          key: 'label',
          renderHeader: renderSpecialHeader,
          render: (h, params) => {
            return h(
              'span',
              {
                style: {
                  background:
                    params.index === 0 ? 'fff' : 'rgba(204, 204, 255, 1)',
                  display: 'inline-block',
                  padding: '2px'
                }
              },
              params.row.label
            )
          }
        },
        {
          title: this.$t('message.streamis.jobListTableColumns.version'),
          key: 'version',
          slot: 'version',
          width: '150px'
        },
        {
          title: this.$t('message.streamis.jobListTableColumns.lastRelease'),
          key: 'createBy',
          renderHeader: renderSpecialHeader
        },
        {
          title: this.$t(
            'message.streamis.jobListTableColumns.lastReleaseTime'
          ),
          key: 'lastVersionTime',
          renderHeader: renderSpecialHeader
        },
        {
          title: this.$t('message.streamis.jobListTableColumns.description'),
          key: 'description'
        },
        {
          title: this.$t('message.streamis.jobListTableColumns.operation'),
          key: 'operation',
          slot: 'operation'
        }
      ],
      jobMoudleRouter: [
        'paramsConfiguration',
        'alertConfiguration',
        'runningHistory',
        'runningLogs',
        'savepoint'
      ],
      pageData: {
        total: 0,
        current: 1,
        pageSize: 10
      },
      loading: false,
      buttonLoading: false,
      poptipVisible: false,
      choosedRowId: '',
      modalVisible: false,
      versionDatas: [],
      uploadVisible: false,
      projectName: this.$route.query.projectName,
      // 作业启动弹框
      startHintVisible: false,
      isBatchRestart: false,
      // 启动弹框的列和数据
      checkColumns: [
        {
          title: '作业名',
          key: 'jobName',
          align: 'center',
          width: 200,
        },
        {
          title: '最新启动版本',
          key: 'latestVersion',
          width: 130,
          align: 'center'
        },
        {
          title: '上次启动版本',
          key: 'lastVersion',
          width: 130,
          align: 'center'
        },
        {
          title: '同名yarn任务',
          slot: 'yarn',
          align: 'center',
          width: 200,
        },
        {
          title: '主备一致性检查',
          slot: 'consistency',
          align: 'center',
          width: 140,
        },
        {
          title: '快照',
          key: 'link',
          align: 'center',
        },
      ],
      hasWarningCount: 0,
      checkData: [],
      // 当前正在查看的data
      currentViewData: {},
      // 失败原因
      failureReason: '',
      failureReasonShow: false,
    }
  },
  async mounted() {
    await this.$nextTick()
    const { jobName, jobStatus, enable, label, jobType, pageNow, pageSize } = this.$route.query
    const hitStatus = allJobStatuses.find(item => item.code === parseInt(jobStatus))
    Object.assign(this.query, {
      jobName,
      label,
      jobType,
      jobStatus: hitStatus ? hitStatus.name : 'all',
      enable: enable === 'true' ? 1 : enable === 'false' ? 0 : enable || 1,
    })
    if(!this.query.enable){
      this.columns.unshift({
        type: 'selection',
        width: 60,
        align: 'center'
      });
    }
    Object.assign(this.pageData, {
      pageNow: parseInt(pageNow) || 1,
      pageSize: parseInt(pageSize) || 10,
    })
    this.getJobList(false)
  },
  methods: {
    // 获取任务列表
    getJobList(shouldPush = true) {
      if (this.loading) {
        return
      }
      this.loading = true
      const { current, pageSize } = this.pageData
      const params = {
        pageNow: current,
        pageSize,
        // 本地开发dev环境用的
        // projectName: 'stream_job',
        // projectName: 'streamis025_checkpoint',
        // 本地开发sit环境用的
        // projectName: 'streamis0606',
        // 正式环境用的
        projectName: this.projectName
      }
      const { jobName, jobStatus, enable, label, jobType } = this.query
      params.jobName = jobName || ''
      params.jobType = jobType || ''
      params.label = label || ''
      params.enable = enable === 'all' ? '' : Boolean(enable)
      if (jobStatus !== 'all') {
        const hitStatus = allJobStatuses.find(item => item.name === jobStatus)
        params.jobStatus = hitStatus.code
      } else {
        params.jobStatus=''
      }


      const toQuery = {
        ...this.$route.query,
        ...params,
        enable: params.enable === '' ? 'all' : params.enable
      }
      if (shouldPush) {
        this.$router.push({
          path: this.$route.path, 
          query: toQuery
        })
      }
      const queries = Object.entries(params)
        .filter(item => !!item[1] || item[1] === 0 || item[1] === false)
        .map(item => item.join('='))
        .join('&')
      api
        .fetch('streamis/streamJobManager/job/list?' + queries, 'get')
        .then(res => {
          if (res) {
            const datas = res.tasks || []
            datas.forEach(item => {
              if (item.lastVersionTime) {
                const newDate = moment(new Date(item.lastVersionTime)).format(
                  'YYYY-MM-DD HH:mm:ss'
                )
                item.lastVersionTime = newDate
              }
            })
            datas.unshift({})
            this.tableDatas = datas.map(r => ({
              ...r,
              poptipVisible: false,
              manageMode: r.manageMode && r.manageMode.toUpperCase() === 'DETACH' ? 'DETACH' : 'ATTACH',
              manageModeChinese: r.manageMode && r.manageMode.toUpperCase() === 'DETACH' ? '分离式' : '非分离式',
              statusObj: allJobStatuses.find(item => item.code === r.status),
            }))
            if (this.tableDatas[0]) {
              delete this.tableDatas[0].manageMode  
              delete this.tableDatas[0].manageModeChinese
              delete this.tableDatas[0].enable
            }
            // console.log(JSON.stringify(datas))
            console.log('this.tableDatas: ', this.tableDatas);
            this.pageData.total = parseInt(res.totalPage)
            this.loading = false
          }
        })
        .catch((err) => {
          console.log('getJobList err: ', err);
          this.loading = false
        })
    },
    goToNewTab(url) {
      window.open(url, new Date().getTime())
    },
    handleQuery() {
      this.$refs['queryForm'].validate(async (valid) => {
        console.log('valid: ', valid);
        if (valid) {
          this.pageData.current = 1
          this.getJobList()
        }
      })

    },
    resetQuery() {
      Object.assign(this.query, {
        jobName: '',
        jobStatus: 'all',
        jobType: '',
        label: '',
        enable: 1
      })
      Object.assign(this.pageData, {
        pageNow: 1,
        pageSize: 10,
      })
      this.hideButtons()
      this.handleQuery()
    },
    handleUpload() {
      this.uploadVisible = true
    },
    handleStop(data, type, index) {
      const { id } = data
      const path = 'streamis/streamJobManager/job/stop'
      data.buttonLoading = true
      this.choosedRowId = id
      data.poptipVisible = false
      this.$set(this.tableDatas, index, data)
      api
        .fetch(path, { jobId: id, snapshot: !!type }, 'get')
        .then(res => {
          data.buttonLoading = false
          this.choosedRowId = ''
          if (res) {
            this.$emit('refreshCoreIndex')
            this.loading = false
            this.getJobList()
          }
        })
        .catch(() => {
          this.loading = false
          data.buttonLoading = false
          this.choosedRowId = ''
          this.getJobList()
        })
    },
    changeVisible(value) {
      console.log('value: ', value);
      if (!value) this.$refs['formValidate'].resetFields()
    },
    // 批量修改标签
    async confirmEditTags() {
      this.editTagsLoading = true
      if (this.formValidate.newTag.includes(' ')) {
        this.editTagsLoading = false;
        return this.$Message.error({ content: '标签不能有空格，仅支持大小写字母、数字、下划线、小数点、逗号' });
      }
      this.$refs['formValidate'].validate(async (valid) => {
        console.log('valid: ', valid);
        if (valid) {
          console.log('this.selections: ', this.selections);
          const tasks = []
          this.selections.forEach(item => {
            tasks.push({
              id: item.id,
              label: this.formValidate.newTag
            })
          })
          // 修改标签接口
          try {
            await api.fetch('/streamis/streamJobManager/job/updateLabel', { tasks }, 'post');
            this.$refs['formValidate'].resetFields();
            this.showEditTags = false;
            this.editTagsLoading = false;
            this.getJobList()
          } catch {
            this.editTagsLoading = false;
          }
        } else {
          this.editTagsLoading = false;
        }
      })
    },


    rowClassName(row){
      if(row.enable === false){
        return 'disabled-row'
      }
      return row.warningRow ? 'warning-row' : ''
    },
    // 1、快照重启和直接重启只有调用接口时snapshot参数的区别
    // 2、快照重启、直接重启、启动 都需要调用inspect接口
    // 3、快照重启、直接重启调用inspect接口前，需要pause接口返回成功；启动 不需要调用pause接口
    // 4、快照重启、直接重启、启动，不管inspections的数据如何，全都放到表格里供用户确认（除了作业名，有三个信息，最新启动版本、上次启动版本和快照，最新启动版本一定是有的，后面两个如果没有就显示为--）；对于没有需要确认信息的作业也显示在表格
    // 5、在“作业启动确认”弹框里，用户点击确认，就启动所有弹框表格里的作业；用户点击取消，就全都不启动；对于没有inspectinos的作业也是一样处理而不是单独启动（上次版本是单独启动）
    // 6、启动是调用/job/execute接口，批量快照重启、批量直接重启是调用/job/bulk/execution接口
    
    // 快照重启和直接重启
    async clickBatchRestart(snapshot) {
      // 3、快照重启、直接重启调用inspect接口前，需要pause接口返回成功
      const bulk_sbj = this.selections.map(item => +item.id);
      if(!this.checkTask(bulk_sbj)){
        this.$Message.error('存在已禁用的任务，请取消勾选已禁用的任务再执行该操作!')
        return
      }
      // 点击批量重启的按钮后，就应该弹出弹窗，pause结束后改变这个一体弹窗的进度，然后开始请求inspect，如果inspect都为空，一体弹窗直接进入下一步启动，如果不为空，上层遮罩再弹出inspect的弹窗需要确认
      this.processModalVisable = true;
      this.modalTitle = this.$t('message.streamis.jobListTableColumns.stopTaskTitle');
      this.modalContent = this.$t('message.streamis.jobListTableColumns.stopTaskContent');

      const pauseRes = await api.fetch('streamis/streamJobManager/job/bulk/pause', { bulk_sbj, snapshot });
      console.log('pause pauseRes', pauseRes);
      if (!pauseRes.result || !pauseRes.result.Success || !pauseRes.result.Failed) throw new Error('停止接口后台返回异常');
      // this.modalLoading = false;
      if (snapshot) {
        this.snapPaths = pauseRes.result.Success.data.map(item => ({
          taskId: item.jobId,
          taskName: item.scheduleId,
          info: item.snapshotPath,
        }));
      }
      this.failTasks = pauseRes.result.Failed.data.map(item => ({
        taskId: item.jobId,
        taskName: item.scheduleId,
        info: item.message,
      }));
      this.orderNum = this.selections.length - this.failTasks.length;
      if (this.failTasks.length) {
        this.isFinish = true;
        this.modalTitle = this.$t('message.streamis.jobListTableColumns.endTaskTitle');
        this.modalContent = this.$t('message.streamis.jobListTableColumns.endTaskTitle');
        return;
      }

      try {
        // 1、快照重启和直接重启只有调用接口时snapshot参数的区别
        await this.handleAction(this.selections, 0, snapshot)
      } catch (error) {
        this.processModalVisable = false;
      }
    },
    clickBatchEditTags(){
      const bulk_sbj = this.selections.map(item => +item.id);
      if(!this.checkTask(bulk_sbj)){
        this.$Message.error('存在已禁用的任务，请取消勾选已禁用的任务再执行该操作!')
        return
      }
      this.showEditTags = true
    },
    async handleAction(data, index, snapshot) {
      console.log('handleAction snapshot: ', snapshot);
      // snapshot有三种情况
      // snapshot为true是 批量-快照重启
      // snapshot为false是 批量-直接重启
      // snapshot === undefined是 点击任务列表的某一项的“启动”
      // snapshot不为undefined时，isBatchRestart就为true
      this.isBatchRestart = snapshot !== undefined
      console.log('handleAction this.isBatchRestart: ', this.isBatchRestart);

      // 存下当前点击的data和index
      this.tempData = data
      this.tempIndex = index
      this.tempSnapshot = snapshot
      this.checkData = []

      if (this.isBatchRestart) {
        // 是批量重启，tempData是数组
        this.hasWarningCount = 0;
        const inspectRes = await Promise.all( this.tempData.map(async (item) => {
          const { id, name } = item
          const checkPath = `streamis/streamJobManager/job/execute/inspect?jobId=${id}`
          const inspectRes = await api.fetch(checkPath, {}, 'put')
          const tempData = {
            id,
            jobName: name,
            link: inspectRes.snapshot && inspectRes.snapshot.path ? inspectRes.snapshot.path : '--',
            latestVersion: inspectRes.version && inspectRes.version.now && inspectRes.version.now.version ? inspectRes.version.now.version : '--',
            lastVersion: inspectRes.version && inspectRes.version.last && inspectRes.version.last.version ? inspectRes.version.last.version : '--',
            yarn: inspectRes.list && inspectRes.list.list ? inspectRes.list.list : [],
            consistency: inspectRes.highavailable && inspectRes.highavailable.highAvailable ? '通过：检查通过' : (inspectRes.highavailable.msg ? '不通过：' + inspectRes.highavailable.msg + '不一致' : '--'),
            warningRow: false
          }
          if(tempData.consistency.includes('不通过')){
            tempData.warningRow = true
            this.hasWarningCount++
          }
          if (Array.isArray(tempData.yarn)) {
            for (let i = 0; i < tempData.yarn.length; i++) {
              if (tempData.yarn[i].applicationId && tempData.yarn[i].applicationId !== '无') {
                tempData.warningRow = true
                this.hasWarningCount++
                break
              }
            }
          }
          this.checkData.push(tempData)
        }))
        console.log('inspectRes: ', inspectRes);
        // 上面虽然是调用多个接口，但因为是await，所以这里的打开弹框的顺序可以按照同步的方式写
        this.startHintVisible = true
        console.log('打开弹框 this.startHintData: ', this.startHintData);
      } else {
        // 是单个重启，tempData是对象
        this.hasWarningCount = 0;
        const { id, name } = this.tempData
        const checkPath = `streamis/streamJobManager/job/execute/inspect?jobId=${id}`
        const inspectRes = await api.fetch(checkPath, {}, 'put')
        const tempData = {
          id,
          jobName: name,
          link: inspectRes.snapshot && inspectRes.snapshot.path ? inspectRes.snapshot.path : '--',
          latestVersion: inspectRes.version && inspectRes.version.now && inspectRes.version.now.version ? inspectRes.version.now.version : '--',
          lastVersion: inspectRes.version && inspectRes.version.last && inspectRes.version.last.version ? inspectRes.version.last.version : '--',
          yarn: inspectRes.list && inspectRes.list.list ? inspectRes.list.list : [],
          consistency: inspectRes.highavailable && inspectRes.highavailable.highAvailable ? '通过：检查通过' : (inspectRes.highavailable.msg ? '不通过：' + inspectRes.highavailable.msg + '不一致' : '--'),
          warningRow: false
        }
        if(tempData.consistency.includes('不通过')){
          tempData.warningRow = true
          this.hasWarningCount++
        }
        if (Array.isArray(tempData.yarn)) {
          for (let i = 0; i < tempData.yarn.length; i++) {
            if (tempData.yarn[i].applicationId && tempData.yarn[i].applicationId !== '无') {
              tempData.warningRow = true
              this.hasWarningCount++
              break
            }
          }
        }
        this.checkData.push(tempData)
        console.log('this.checkData: ', this.checkData);
        this.startHintVisible = true
        console.log('打开弹框 this.startHintData: ', this.startHintData);
      }
    },
    cancelStartHint() {
      this.startHintVisible = false
      this.processModalVisable = false
      this.checkData = []
    },
    // 检查弹框的确认按钮
    async confirmStarting() {
      this.startHintVisible = false
      if (!this.isBatchRestart) {
        // 如果是单个的启动，那么直接调用execute启动接口
        const { id } = this.tempData
        const second = { jobId: id }

        const path = 'streamis/streamJobManager/job/execute'
        this.tempData.buttonLoading = true
        this.choosedRowId = id
        this.$set(this.tableDatas, this.tempIndex, this.tempData)
        try {
          api
            .fetch(path, second)
            .then(res => {
              console.log('execute res: ', res);
              this.tempData.buttonLoading = false
              this.choosedRowId = ''
              if (res) {
                this.$emit('refreshCoreIndex')
                this.loading = false
                this.getJobList()
              }
            })
            .catch(err => {
              console.log('err: ', err);
              this.loading = false
              this.tempData.buttonLoading = false
              this.choosedRowId = ''
              this.getJobList()
              throw new Error(err)
            })
        } catch (error) {
          console.log('error: ', error);
          this.$Message.error({ content: '后台接口异常，请联系开发处理！' });
        }
      } else {
        // 批量启动
        console.log('bulkExecution');
        try {
          const bulk_sbj = this.selections.map(item => +item.id);
          const result = await api.fetch('streamis/streamJobManager/job/bulk/execution', { bulk_sbj });
          console.log('start result', result);
          this.queryProcess(bulk_sbj);
        } catch (err) {
          console.log('bulkExecution err: ', err);
          this.modalContent = '停止任务失败，失败信息：' + err
        }
      }
    },
    handleConfig() {
    },
    stopSavepoint(data) {
      this.loading = true
      api
        .fetch(
          'streamis/streamJobManager/job/snapshot/' + data.id,
          {},
          'put'
        )
        .then(res => {
          console.log('stopSavepoint res: ', res);
          if (res) {
            this.loading = false
            this.snapModalVisable = true;
            this.snapTitle = this.$t('message.streamis.jobListTableColumns.snapTitle');
            this.snapshotPath = res.path;
            this.getJobList();
          }
        })
        .catch(err => {
          console.log('stopSavepoint err: ', err);
          this.loading = false
        })
    },
    checkTask(ids){
      const resArray = this.tableDatas.filter(item=> ids.includes(item.id)).map(item=>item.enable)
      console.log(ids)
      return resArray.includes(false) ? false : true // 存在禁用的任务，批量操作任务预检查失败，不能执行批量操作
    },
    enableTask(ids){
      this.loading = true
      api.fetch(
        '/streamis/streamJobManager/job/enable',
        ids,
        'post'
      ).then(res=>{
        console.log('enableTask res: ', res);
        if (res) {
          this.loading = false
          this.getJobList();
        }
      }).catch(err => {
        console.log('enableTask err: ', err);
        this.loading = false
      })
    },
    disableTask(ids){
      this.loading = true
      api.fetch(
        '/streamis/streamJobManager/job/ban',
        ids,
        'post'
      ).then(res=>{
        console.log('disableTask res: ', res);
        if (res) {
          this.loading = false
          this.getJobList();
        }
      }).catch(err => {
        console.log('disableTask err: ', err);
        this.loading = false
      })
    },
    handleRouter(rowData, moduleName) {
      if(moduleName === 'savepoint'){
        this.stopSavepoint(rowData);
        return;
      }
      if(moduleName === 'enable'){
        this.enableTask([rowData.id])
        return
      }
      if(moduleName === 'disable'){
        this.disableTask([rowData.id])
        return
      }
      const moduleMap = {
        paramsConfiguration: 'jobConfig',
        alertConfiguration: 'jobConfig',
        runningHistory: 'jobHistory',
        runningLogs: 'jobHistory'
      }
      this.$router.push({
        name: 'JobDetail',
        params: {
          id: rowData.id,
          module: moduleName
            ? moduleMap[moduleName] || moduleName
            : 'jobSummary',
          name: rowData.name,
          version: rowData.version,
          status: rowData.status,
          jobType: rowData.jobType,
          manageMode: rowData.manageMode,
          projectName: rowData.projectName || this.projectName,
          enable: rowData.enable,
        }
      })
    },
    handlePageChange(page) {
      console.log('handlePageChange page: ', page);
      this.pageData.current = page
      this.getJobList()
    },
    handlePageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize
      this.pageData.current = 1
      this.getJobList()
    },
    showFailureReason(row){
      api.fetch(`streamis/streamJobManager/job/execute/errorMsg?jobId=${row.id}`,'get').then(
        res=>{
          console.log(res)
          this.failureReason = res.details.errDesc || '原因未知'
        }
      ).catch(err=>{
        console.log('showFailureReason err:',err)
      }).finally(()=>{
        this.failureReasonShow = true
      })
    },
    versionDetailPageChange(page) {
      this.versionDetail(this.currentViewData, page)
    },
    versionDetail(data, page) {
      console.log('data: ', data);
      console.log('data.versionForwards: ', data.versionForwards);
      this.currentViewData = data
      this.loading = true
      api.fetch(`streamis/streamJobManager/job/${data.id}/versions?pageNow=${page ? page : 1}&pageSize=5`, 'get').then(res => {
        console.log('versionDetail versions res: ', res);
        this.loading = false
        this.modalVisible = true
        this.$refs.versionDetail.pageData.total = res.totalPage
        this.versionDatas = res.versions
        this.versionDatas.forEach(item => {
          item.versionStatus = '--'
          if (item.version === data.version) item.versionStatus = this.$t('message.streamis.versionDetail.using')
        })
      }).catch(err => {
        console.log('versionDetail versions err: ', err);
        this.loading = false
      })
    },
    modalCancel() {
      this.modalVisible = false
    },
    jarModalCancel() {
      this.uploadVisible = false
    },
    jarUploadSuccess(res) {
      if (res && res.status !== 0 && res.message) {
        this.$Message.error(res.message)
      }
      this.getJobList()
    },
    jarUploadError(err, res) {
      if (res && res.status !== 0 && res.message) {
        this.$Message.error(res.message)
      }
    },
    showButtons() {
      this.columns.unshift({
        type: 'selection',
        width: 60,
        align: 'center'
      });
      this.isBatching = true;
    },
    hideButtons() {
      this.$refs.list.selectAll(false);
      this.selections = [];
      this.columns = this.columns.filter(col => col.type !== 'selection');
      this.isBatching = false;
    },
    selectionChange(val) {
      const selections = (val || []).filter(item => item.id);
      this.selections = selections;
    },
    onEnableStatusChange(val){
      if(val === 0){
        this.columns.unshift({
          type: 'selection',
          width: 60,
          align: 'center'
        });
      } else {
        this.hideButtons()
      }
      this.pageData.current = 1
      this.getJobList()
    },
    clickBatchEnable(){
      this.enableTask(this.selections.map(item=>item.id))
    },
    clickBatchDisable(){
      const bulk_sbj = this.selections.map(item => +item.id);
      if(!this.checkTask(bulk_sbj)){
        this.$Message.error('存在已禁用的任务，请取消勾选已禁用的任务再执行该操作!')
        return
      }
      this.disableTask(this.selections.map(item=>item.id))
    },
    async queryProcess(id_list) {
      console.log('queryProcess');
      this.modalTitle = this.$t('message.streamis.jobListTableColumns.startTaskTitle');
      this.modalContent = this.$t('message.streamis.jobListTableColumns.startTaskContent');
      this.orderNum = 0;
      this.failTasks = [];
      try {
        // this.modalLoading = true;
        const res = await api.fetch('streamis/streamJobManager/job/status', { id_list }, 'put');
        if (!res.result || !Array.isArray(res.result)) throw new Error('后台返回异常');
        res.result.forEach((item) => {
          if ([1, 5, 6, 7].includes(item.statusCode)) this.orderNum++;
        })
        this.failTasks = res.result.filter(item => [6, 7].includes(item.statusCode)).map(item => ({
          taskId: item.jobId,
          taskName: item.status,
          info: item.message,
        }));
        if (this.orderNum < this.selections.length) {
          this.timer = setTimeout(() => {
            this.queryProcess(id_list);
          }, 2500);
        } else {
          // this.modalLoading = false;
          this.timer = null;
          this.isFinish = true;
          this.modalTitle = this.$t('message.streamis.jobListTableColumns.endTaskTitle');
          this.modalContent = this.$t('message.streamis.jobListTableColumns.endTaskTitle');
        }
      } catch (error) {
        console.warn(error)
        // this.modalLoading = false
      }
    },
    onClose(status) {
      if (status) return;
      this.processModalVisable = false;
      this.snapPaths = []
      this.isFinish = false;
      clearTimeout(this.timer);
      this.failTasks = [];
      this.orderNum = 0;
      this.hideButtons();
      this.getJobList()
    }
  }
}
</script>
<style lang="scss" scoped>
.text{
  word-break: break-all;
}
.select {
  width: 200px;
}
.jobName {
  display: flex;
  cursor: pointer;
}
.more {
  font-size: 20px;
}
.upload {
  font-size: 24px;
}
.page {
  margin-top: 20px;
}
.failureReasonWrapper{
  color: red;
  cursor: pointer;
}
.versionWrap {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  position: relative;
}
.version {
  background-color: #008000;
  text-align: center;
  color: #ffffff;
  font-size: 16px;
  cursor: pointer;
  padding: 0px 3px;
}
.versionForwards {
  color: red;
  font-size: 12px;
  position: relative;
  top: -5px;
  // left: 5px;
  margin-left: 5px;
  // // z-index: 9999;
  // position: absolute;
  // left: 65px;
  // top: -4px;
}
.btn-wrap {
  display: flex;
  flex-direction: column;
  .btn {
    display: block;
    margin-bottom: 5px;
  }
}
.wrap {
  .general {
    margin-bottom: 32px;
    .text {
      font-weight: bold;
    }
  }
}
.table {
  /deep/ .ivu-table-tbody {
    tr:first-child {
      td:first-child {
        div:first-child {
          visibility: hidden;
        }
      }
    }
  }
}
.red-color {
  color: red;
}
.warning-color{
  color: #ff9900;
}

/deep/.ivu-table .warning-row td{
  background-color: #ff9900;
  border-right: none;
}
/deep/.ivu-table .disabled-row td{
  background-color: #eee;
  border-right: none;
}
.modal-footer-wrapper{
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: space-between;
  .modal-footer-descs{
    display: flex;
    align-items: center;
    justify-content: flex-start;
  }
  .modal-footer-btns{
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }
}
</style>

<style lang="scss">
.button-item {
  .ivu-form-item-content {
    margin-left: 16px !important;
  }
}
.joblist-table-ivu-select-dropdown{
  max-height: 400px;
}
</style>