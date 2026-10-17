import '@testing-library/jest-dom';

// Mock ResizeObserver for React Flow in test environments
class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

window.ResizeObserver = ResizeObserver;
