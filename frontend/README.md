# DatShop Frontend

React frontend for the DatShop marketplace backend. It uses Axios for REST calls, React Router for navigation, and STOMP over SockJS for real-time chat.

## Included experiences

- Product discovery, search, filtering, details, follows, and cart actions
- Registration, login, JWT refresh, and logout
- Cart quantity management and checkout with coupons and VNPay
- Customer orders and GHN delivery tracking
- Seller product CRUD, shop orders, coupons, GHN delivery management, and Selenium imports
- User profiles, follows, votes/reviews, and direct conversations
- Mistral/Gemini chatbot and RAG knowledge ingestion

## Local setup

Requires Node.js 20.19 or newer.

```powershell
Copy-Item .env.example .env
pnpm install
pnpm dev
```

The frontend defaults to `http://localhost:5173` and connects to the backend at `http://localhost:8080`. Change `VITE_API_URL` in `.env` when the backend uses another address.

## Commands

```powershell
pnpm dev      # development server
pnpm build    # production build
pnpm lint     # static checks
pnpm preview  # preview the production build
```
