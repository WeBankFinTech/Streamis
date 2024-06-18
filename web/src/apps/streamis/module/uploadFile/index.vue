<template>
  <div>
    <Modal
      :title="$t('message.streamis.jobListTableColumns.upload')"
      v-model="visible"
      @on-cancel="cancel"
      footer-hide
      width="400"
    >
      <Form
        ref="uploadForm"
        :model="uploadForm"
        :rules="ruleValidate"
        :label-width="100"
      >
        <FormItem :label="$t('message.streamis.projectFile.file')">
          <Upload :before-upload="handleUpload">
            <Button icon="ios-cloud-upload-outline" style="width:270px">{{
              $t('message.streamis.projectFile.chooseUploadFile')
            }}</Button>
          </Upload>
          <div v-if="!!file" class="choosed-file" :title="file.name">
            {{ $t('message.streamis.uploadJar.choosedFile') }}: {{ file.name }}
          </div>
        </FormItem>
        <FormItem
          :label="$t('message.streamis.projectFile.specifyVersion')"
          prop="version"
        >
          <Input
            v-model="uploadForm.version"
            :placeholder="$t('message.streamis.projectFile.versionPlaceholder')"
          ></Input>
        </FormItem>
        <FormItem
          :label="$t('message.streamis.projectFile.versionDescription')"
          prop="comment"
        >
          <Input type="textarea" v-model="uploadForm.comment"></Input>
        </FormItem>
        <FormItem
          :label="$t('message.streamis.projectFile.overrideImport')"
          prop="updateWhenExists"
        >
          <Checkbox v-model="uploadForm.updateWhenExists"></Checkbox>
        </FormItem>
        <FormItem>
          <Button type="primary" @click="handleSubmit()" :loading="loading">{{
            $t('message.streamis.uploadJar.upload')
          }}</Button>
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>
<script>
import api from '@/common/service/api'
export default {
  props: {
    visible: Boolean,
    projectName: String
  },
  data() {
    return {
      uploadForm: {
        version: '',
        updateWhenExists: false,
        comment: ''
      },
      file: '',
      ruleValidate: {
        version: [
          {
            required: true,
            message: this.$t('message.streamis.projectFile.versionEmpty'),
            trigger: 'blur'
          },
          {
            validator: this.checkVersionName,
            trigger: 'blur',
          }
        ],
        comment: [
          {
            validator: this.checkComment,
            trigger: 'blur',
          }
        ]
      },
      loading: false
    }
  },
  methods: {
    handleSubmit() {
      console.log(this.projectName);
      this.$refs['uploadForm'].validate(valid => {
        if (!this.file) {
          this.$Message.error(this.$t('message.streamis.projectFile.fileEmpty'))
          return
        }
        if (valid) {
          const formData = new FormData()
          formData.append('file', this.file)
          formData.append('fileName', this.file.name)
          formData.append('projectName', this.projectName)
          Object.keys(this.uploadForm).forEach(key => {
            const value = this.uploadForm[key]
            formData.append(key, value)
          })
          this.loading = true
          api
            .fetch(
              'streamis/streamProjectManager/project/files/upload',
              formData,
              {headers: {'Content-Type': 'multipart/form-data'}}
            )
            .then(res => {
              this.loading = false
              this.$t('message.streamis.operationSuccess')
              this.cancel()
              this.$emit('fileUploadSuccess')
              console.log(res)
            })
            .catch(e => {
              console.log(e)
              this.loading = false
            })
        }
      })
    },
    handleUpload(file) {
      this.file = file
      console.log(file)
      return false
    },
    cancel() {
      this.file = ''
      this.$refs['uploadForm'].resetFields()
      this.$emit('fileModalCancel')
    },
    checkVersionName(rule, value, callback){
      const _this = this;
      const reg = new RegExp('^[0-9.]*$');
      if(!reg.test(value)){
        return callback(new Error(_this.$t('message.streamis.projectFile.versionPlaceholder')))
      }else{
        callback()
      }
    },
    checkComment(rule, value, callback){
      const _this = this;
      if(value.length > 50){
        return callback(new Error(_this.$t('message.streamis.projectFile.commentLength')))
      }else{
        callback()
      }
    },
  }
}
</script>
<style lang="scss" scoped>
.choosed-file {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>
