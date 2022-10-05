import ReactDOM from 'react-dom/client';
import './index.css';
import 'csh-material-bootstrap/dist/csh-material-bootstrap.css';
import App from './pages/App';
import { OidcProvider, OidcSecure } from '@axa-fr/react-oidc';
import configuration from './config';

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
  <OidcProvider configuration={configuration}>
    <OidcSecure>
      <App />
    </OidcSecure>
  </OidcProvider>
);