<template>
  <TextField
    :model-value="formattedDate"
    :label="label"
    type="date"
    :placeholder="placeholder"
    :required="required"
    :disabled="disabled"
    :readonly="readonly"
    :error="error"
    :hint="hint"
    @update:model-value="onDateChange"
    v-bind="$attrs"
  />
</template>

<script setup>
import { computed } from 'vue'
import TextField from './TextField.vue'

const props = defineProps({
  modelValue: [String, Date],
  label: String,
  placeholder: String,
  required: Boolean,
  disabled: Boolean,
  readonly: Boolean,
  error: String,
  hint: String
})

const emit = defineEmits(['update:modelValue'])

const formattedDate = computed(() => {
  if (!props.modelValue) return ''
  if (props.modelValue instanceof Date) {
    return props.modelValue.toISOString().split('T')[0]
  }
  return props.modelValue
})

const onDateChange = (value) => {
  emit('update:modelValue', value)
}
</script>
