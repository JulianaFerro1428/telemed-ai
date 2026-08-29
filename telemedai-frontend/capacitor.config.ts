import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.telemed.ia',
  appName: 'TeleMed IA',
  webDir: 'dist/telemed-frontend/browser',
  server: { androidScheme: 'https' }
};

export default config;
