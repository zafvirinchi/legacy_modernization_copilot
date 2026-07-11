# Frontend Architecture

## Overview

The frontend is a Next.js 14+ application with TypeScript, TailwindCSS, and shadcn/ui.
It follows modern React patterns including:
- Server and Client Components
- App Router
- Client-side state management with Context API
- Custom hooks for reusable logic
- Centralized API service layer

## Directory Structure

### `/src/app`
Next.js 14 App Router pages and layouts.

**Route Groups**:
- `(dashboard)/` - Protected dashboard routes
- `(auth)/` - Authentication routes (login, register)

**Special Files**:
- `layout.tsx` - Root layout with providers
- `page.tsx` - Home page
- `error.tsx` - Global error boundary
- `not-found.tsx` - 404 page
- `providers.tsx` - Client providers setup

### `/src/components`
Reusable React components organized by feature.

**Structure**:
- `common/` - Shared components (Header, Sidebar, Footer, Loader)
- `dashboard/` - Dashboard-specific components
- `upload/` - File upload components
- `analysis/` - Analysis view components
- `reports/` - Report display components
- `architecture-viewer/` - Architecture diagram visualization
- `ui/` - shadcn/ui imported components

### `/src/hooks`
Custom React hooks for common patterns:
- `useAsync` - Manage async operations
- `useLocalStorage` - Persist state to localStorage
- `useDebounce` - Debounce values
- `usePrevious` - Track previous values

### `/src/services`
API service layer with centralized endpoints:
- `authService` - Login, register, logout
- `projectService` - CRUD for projects
- `scanService` - Scan operations
- `analysisService` - Get issues and recommendations
- `reportService` - Generate and download reports

### `/src/context`
React Context for global state:
- `AuthContext` - User authentication state

### `/src/types`
TypeScript type definitions:
- `Project`, `Scan`, `Issue`, `Recommendation`, `Report`, `User`

### `/src/constants`
Application constants:
- API endpoints
- UI configuration
- Navigation items
- Enumeration values

### `/src/utils`
Utility functions:
- Authentication token management
- Date/time formatting
- String manipulation
- Validation helpers

### `/src/lib`
Library configurations and helpers:
- `api-client.ts` - Axios instance with interceptors

### `/src/styles`
Global and component styles:
- `globals.css` - TailwindCSS base styles
- `components.css` - Component utility styles

### `/src/providers`
Custom React providers:
- `tailwind-provider.tsx` - Theme management

## Data Flow

```
Component
    ↓
Custom Hook / Context
    ↓
Service Layer (API calls)
    ↓
API Client (Axios)
    ↓
Backend API
```

## Key Features

### Authentication Flow
1. User submits login form
2. `authService.login()` calls backend
3. JWT token stored in localStorage
4. `AuthContext` updated with user
5. User redirected to dashboard

### Data Fetching Pattern
```typescript
const { data, loading, error } = useAsync(
  () => projectService.list(),
  true
);
```

### Form Submission with Validation
```typescript
const form = useForm<FormData>({
  resolver: zodResolver(schema),
});

const onSubmit = async (data: FormData) => {
  try {
    const result = await projectService.create(data);
  } catch (error) {
    form.setError('root', { message: error.message });
  }
};
```

## Styling Approach

### TailwindCSS
- Utility-first CSS framework
- Configured with custom colors and spacing
- Dark mode support with `dark:` prefix

### shadcn/ui
- Headless, unstyled components
- Built with Radix UI
- Customizable with TailwindCSS

### Component Styles
```typescript
<div className="flex items-center justify-between rounded-md bg-primary px-4 py-2 text-white">
  Button
</div>
```

## Performance Optimizations

- **Code Splitting** - Next.js automatic route-based splitting
- **Image Optimization** - next/image for responsive images
- **Dynamic Imports** - Lazy load components
- **API Caching** - Service layer can implement caching
- **Debouncing** - useDebounce for search queries

## Testing Strategy

### Unit Tests
- Component rendering
- Hook functionality
- Utility functions

### Integration Tests
- Full page flows
- API integration
- Form submissions

## Development Best Practices

1. **Component Naming** - PascalCase for components
2. **File Organization** - Feature-based structure
3. **Imports** - Use path aliases (@/)
4. **Error Handling** - Try-catch with user feedback
5. **Accessibility** - Semantic HTML and ARIA labels
6. **Performance** - Minimize re-renders with React.memo
7. **Types** - Full TypeScript coverage
8. **Documentation** - JSDoc comments for complex functions

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Accessibility

- Semantic HTML elements
- ARIA labels where needed
- Keyboard navigation support
- Color contrast compliance
- Focus management

## Environment Variables

See `.env.example` for complete list of required variables.

## Deployment Checklist

- [ ] Environment variables configured
- [ ] API endpoint verified
- [ ] Dark mode tested
- [ ] Responsive design verified
- [ ] Accessibility check completed
- [ ] Performance optimizations applied
- [ ] Error handling tested
- [ ] Security headers configured
