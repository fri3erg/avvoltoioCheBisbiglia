jest.mock('app/core/auth/account.service');

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { throwError, of } from 'rxjs';
import { TranslateModule } from '@ngx-translate/core';

import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';

import SettingsComponent from './settings.component';

describe('SettingsComponent', () => {
  let comp: SettingsComponent;
  let fixture: ComponentFixture<SettingsComponent>;
  let mockAccountService: AccountService;
  const account: Account = {
    first_name: 'John',
    last_name: 'Doe',
    activated: true,
    email: 'john.doe@mail.com',
    lang_key: 'en',
    login: 'john',
    authorities: [],
    image_url: '',
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot(), HttpClientTestingModule, SettingsComponent],
      providers: [FormBuilder, AccountService],
    })
      .overrideTemplate(SettingsComponent, '')
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SettingsComponent);
    comp = fixture.componentInstance;
    mockAccountService = TestBed.inject(AccountService);
    mockAccountService.identity = jest.fn(() => of(account));
    mockAccountService.getAuthenticationState = jest.fn(() => of(account));
  });

  it('should send the current identity upon save', () => {
    // GIVEN
    mockAccountService.save = jest.fn(() => of({}));
    const settingsFormValues = {
      first_name: 'John',
      last_name: 'Doe',
      email: 'john.doe@mail.com',
      lang_key: 'en',
    };

    // WHEN
    comp.ngOnInit();
    comp.save();

    // THEN
    expect(mockAccountService.identity).toHaveBeenCalled();
    expect(mockAccountService.save).toHaveBeenCalledWith(account);
    expect(mockAccountService.authenticate).toHaveBeenCalledWith(account);
    expect(comp.settingsForm.value).toMatchObject(expect.objectContaining(settingsFormValues));
  });

  it('should notify of success upon successful save', () => {
    // GIVEN
    mockAccountService.save = jest.fn(() => of({}));

    // WHEN
    comp.ngOnInit();
    comp.save();

    // THEN
    expect(comp.success).toBe(true);
  });

  it('should notify of error upon failed save', () => {
    // GIVEN
    mockAccountService.save = jest.fn(() => throwError('ERROR'));

    // WHEN
    comp.ngOnInit();
    comp.save();

    // THEN
    expect(comp.success).toBe(false);
  });
});
