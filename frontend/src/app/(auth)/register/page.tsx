'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { isAxiosError } from 'axios';
import { z } from 'zod';
import { useAuth } from '@/context/auth-context';

const ROLE_OPTIONS = ['DEVELOPER', 'ARCHITECT', 'ADMIN'] as const;

const registerSchema = z
  .object({
    name: z.string().min(1, 'Name is required').max(100),
    email: z.string().min(1, 'Email is required').email('Enter a valid email'),
    password: z.string().min(8, 'Password must be at least 8 characters'),
    confirmPassword: z.string().min(1, 'Please confirm your password'),
    role: z.enum(ROLE_OPTIONS),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Passwords do not match',
    path: ['confirmPassword'],
  });

type RegisterFormValues = z.infer<typeof registerSchema>;

export default function RegisterPage() {
  const router = useRouter();
  const { register: registerUser } = useAuth();
  const [serverError, setServerError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: { role: 'DEVELOPER' },
  });

  const onSubmit = async (values: RegisterFormValues) => {
    setServerError(null);
    try {
      await registerUser(values.name, values.email, values.password, values.role);
      router.push('/dashboard');
    } catch (error: unknown) {
      const message = isAxiosError<{ message?: string }>(error)
        ? error.response?.data?.message
        : undefined;
      setServerError(message ?? 'Unable to create account');
    }
  };

  return (
    <div className="w-full max-w-sm rounded-lg border border-border bg-card p-8">
      <h1 className="text-xl font-semibold">Create an account</h1>
      <p className="mt-1 text-sm text-muted-foreground">Get started with AI Legacy Modernization Copilot</p>

      <form className="mt-6 flex flex-col gap-4" onSubmit={handleSubmit(onSubmit)} noValidate>
        <div>
          <label htmlFor="name" className="text-sm font-medium">
            Name
          </label>
          <input
            id="name"
            autoComplete="name"
            className="mt-1 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
            {...register('name')}
          />
          {errors.name && <p className="mt-1 text-xs text-destructive">{errors.name.message}</p>}
        </div>

        <div>
          <label htmlFor="email" className="text-sm font-medium">
            Email
          </label>
          <input
            id="email"
            type="email"
            autoComplete="email"
            className="mt-1 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
            {...register('email')}
          />
          {errors.email && <p className="mt-1 text-xs text-destructive">{errors.email.message}</p>}
        </div>

        <div>
          <label htmlFor="password" className="text-sm font-medium">
            Password
          </label>
          <input
            id="password"
            type="password"
            autoComplete="new-password"
            className="mt-1 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
            {...register('password')}
          />
          {errors.password && <p className="mt-1 text-xs text-destructive">{errors.password.message}</p>}
        </div>

        <div>
          <label htmlFor="confirmPassword" className="text-sm font-medium">
            Confirm password
          </label>
          <input
            id="confirmPassword"
            type="password"
            autoComplete="new-password"
            className="mt-1 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
            {...register('confirmPassword')}
          />
          {errors.confirmPassword && (
            <p className="mt-1 text-xs text-destructive">{errors.confirmPassword.message}</p>
          )}
        </div>

        <div>
          <label htmlFor="role" className="text-sm font-medium">
            Role
          </label>
          <select
            id="role"
            className="mt-1 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
            {...register('role')}
          >
            {ROLE_OPTIONS.map((role) => (
              <option key={role} value={role}>
                {role.charAt(0) + role.slice(1).toLowerCase()}
              </option>
            ))}
          </select>
          {errors.role && <p className="mt-1 text-xs text-destructive">{errors.role.message}</p>}
        </div>

        {serverError && <p className="text-sm text-destructive">{serverError}</p>}

        <button
          type="submit"
          disabled={isSubmitting}
          className="mt-2 w-full rounded-md bg-primary px-4 py-2 text-sm font-medium text-primary-foreground disabled:opacity-50"
        >
          {isSubmitting ? 'Creating account...' : 'Create account'}
        </button>
      </form>

      <p className="mt-6 text-center text-sm text-muted-foreground">
        Already have an account? <Link href="/login">Sign in</Link>
      </p>
    </div>
  );
}
