import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user.model';

/** Requires at least 8 chars with an uppercase, lowercase, number and symbol. */
const STRONG_PASSWORD_PATTERN =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,}$/;

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  private userService = inject(UserService);
  private fb = inject(FormBuilder);

  profile: User | null = null;
  loading = true;
  saving = false;
  successMessage = '';
  errorMessage = '';

  // The backend requires the password on every profile update, so the
  // user re-enters their current password to confirm changes, or a new
  // one if they want to change it.
  form = this.fb.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.pattern(STRONG_PASSWORD_PATTERN)]]
  });

  ngOnInit(): void {
    this.userService.getMyProfile().subscribe((profile) => {
      this.profile = profile;
      this.form.patchValue({ name: profile.name, email: profile.email });
      this.loading = false;
    });
  }

  save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    this.successMessage = '';
    this.errorMessage = '';

    const value = this.form.getRawValue();
    this.userService
      .updateMyProfile({
        name: value.name!,
        email: value.email!,
        password: value.password!
      })
      .subscribe({
        next: (updated) => {
          this.saving = false;
          this.profile = updated;
          this.successMessage = 'Profile updated successfully.';
          this.form.get('password')?.setValue('');
        },
        error: (err: HttpErrorResponse) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to update profile.';
        }
      });
  }
}
