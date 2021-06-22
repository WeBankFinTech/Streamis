<template>
  <div>
    <Modal
      :title="$t('message.streamis.jobListTableColumns.upload')"
      v-model="visible"
      @on-cancel="cancel"
      footer-hide
      width="800"
    >
      <Form
        ref="uploadForm"
        :model="uploadForm"
        :rules="ruleValidate"
        :label-width="80"
      >
        <FormItem
          :label="$t('message.streamis.jobListTableColumns.jobName')"
          prop="jobName"
        >
          <Input v-model="uploadForm.jobName"></Input>
        </FormItem>
        <FormItem label="E-mail" prop="mail">
          <Input v-model="uploadForm.mail"></Input>
        </FormItem>
        <FormItem label="E-mail" prop="mail">
          <Upload
            :before-upload="handleUpload"
            action="//jsonplaceholder.typicode.com/posts/"
          >
            <Button icon="ios-cloud-upload-outline"
            >Select the file to upload</Button
            >
          </Upload>
          <div v-if="file !== null">
            Upload file: {{ file.name }}
            <Button type="text" @click="upload" :loading="loadingStatus">{{
              loadingStatus ? "Uploading" : "Click to upload"
            }}</Button>
          </div>
        </FormItem>
        <FormItem>
          <Button type="primary" @click="handleSubmit()">Submit</Button>
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>
<script>
export default {
  props: {
    visible: Boolean,
  },
  data() {
    return {
      uploadForm: {
        jobName: "",
        entrypointClass: "",
        label: "",
        entrypointMainArgs: "",
        parallelism: "",
        file: ""
      },
      file: "",
      ruleValidate: {
        jobName: [
          {
            required: true,
            message: "The name cannot be empty",
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
    handleSubmit() {
      this.$refs["uploadForm"].validate(valid => {
        if (valid) {
          this.$Message.success("Success!");
        } else {
          this.$Message.error("Fail!");
        }
      });
    },
    handleUpload (file) {
      this.file = file;
      return false;
    },
    upload () {
      this.loadingStatus = true;
      setTimeout(() => {
        this.file = null;
        this.loadingStatus = false;
        this.$Message.success('Success')
      }, 1500);
    },
    cancel() {
      this.$emit('jarModalCancel');
    }
  }
};
</script>
<style lang="scss" scoped></style>
