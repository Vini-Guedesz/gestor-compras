<template>
  <div class="wizard-progress">
    <div class="progress-bar">
      <div
        v-for="step in steps"
        :key="step.number"
        class="progress-step"
        :class="{
          'active': step.number === currentStep,
          'completed': step.number < currentStep
        }"
      >
        <div class="step-circle">
          <span v-if="step.number < currentStep" class="check-icon">✓</span>
          <span v-else>{{ step.number }}</span>
        </div>
        <div class="step-label">{{ step.label }}</div>
        <div v-if="step.number < totalSteps" class="step-line"></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WizardProgress',
  props: {
    currentStep: {
      type: Number,
      required: true,
      default: 1
    },
    totalSteps: {
      type: Number,
      required: true,
      default: 3
    },
    stepLabels: {
      type: Array,
      default: () => ['Criar Pedido', 'Aprovar Itens', 'Adicionar Cotações']
    }
  },
  computed: {
    steps() {
      return this.stepLabels.map((label, index) => ({
        number: index + 1,
        label: label
      }))
    }
  }
}
</script>

<style scoped>
.wizard-progress {
  padding: 24px 0;
  margin-bottom: 24px;
  border-bottom: 1px solid #e5e7eb;
}

.progress-bar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  position: relative;
  max-width: 600px;
  margin: 0 auto;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  flex: 1;
}

.step-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e5e7eb;
  color: #6b7280;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 1rem;
  transition: all 0.3s ease;
  z-index: 2;
  position: relative;
}

.progress-step.active .step-circle {
  background: #1F285F;
  color: white;
  box-shadow: 0 0 0 4px rgba(31, 40, 95, 0.2);
}

.progress-step.completed .step-circle {
  background: #10b981;
  color: white;
}

.check-icon {
  font-size: 1.25rem;
}

.step-label {
  margin-top: 8px;
  font-size: 0.875rem;
  color: #6b7280;
  font-weight: 500;
  text-align: center;
  white-space: nowrap;
  transition: color 0.3s ease;
}

.progress-step.active .step-label {
  color: #1F285F;
  font-weight: 600;
}

.progress-step.completed .step-label {
  color: #10b981;
}

.step-line {
  position: absolute;
  top: 20px;
  left: 60%;
  right: -40%;
  height: 2px;
  background: #e5e7eb;
  transition: background 0.3s ease;
  z-index: 1;
}

.progress-step.completed .step-line {
  background: #10b981;
}

/* Responsive */
@media (max-width: 768px) {
  .progress-bar {
    max-width: 100%;
    padding: 0 16px;
  }

  .step-circle {
    width: 32px;
    height: 32px;
    font-size: 0.875rem;
  }

  .step-label {
    font-size: 0.75rem;
    max-width: 80px;
    white-space: normal;
    line-height: 1.2;
  }

  .step-line {
    top: 16px;
  }
}
</style>
