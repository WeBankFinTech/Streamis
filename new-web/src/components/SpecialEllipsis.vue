<template>
    <div
        ref="ellipsis"
        class="ellipsis"
        :class="isOverflow ? 'overflow' : ''"
        @click="handleClick"
    >
        {{content}}
    </div>
</template>
<script setup>
import {
    onUpdated, nextTick, ref, watch, watchEffect,
} from 'vue';

// eslint-disable-next-line no-undef
const props = defineProps({
    content: {
        type: String,
        default: '',
    },
});

// eslint-disable-next-line no-undef
const emit = defineEmits(['showDetail']);

const ellipsis = ref(null);
const isOverflow = ref(false);
const obsever = ref(null);

watchEffect(() => {
    const element = ellipsis.value;
    if (element) {
        const handleResize = () => {
            // console.log(element?.scrollWidth, element?.clientWidth);
            if (element?.scrollWidth > element?.clientWidth) {
                isOverflow.value = true;
            } else {
                isOverflow.value = false;
            }
        };
        obsever.value = new ResizeObserver(handleResize);
        obsever.value.observe(element, { box: 'border-box' });
        handleResize();
        return () => {
            obsever.value.disconnect();
        };
    }
});

const handleClick = () => {
    emit('showDetail', isOverflow.value);
};

</script>

<style lang='less' scoped>
.ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
}

.overflow {
    color: #5384ff;
    cursor: pointer;
}
</style>
