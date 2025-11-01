import React from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import App from './App'

import './styles/base/variables.css'
import './styles/base/reset.css'
import './styles/base/utilities.css'

import './styles/components/header.css'
import './styles/components/footer.css'
import './styles/components/navigation.css'
import './styles/components/buttons.css'
import './styles/components/forms.css'
import './styles/components/product-card.css'
import './styles/components/carousel.css'
import './styles/components/modal.css'
import './styles/components/skeleton.css'
import './styles/components/tags.css'

import './styles/pages/home.css'

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
)
